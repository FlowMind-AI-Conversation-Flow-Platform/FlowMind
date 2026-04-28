package com.flowmind.dispatch.service;

import com.flowmind.dispatch.model.FallbackReason;
import com.flowmind.dispatch.model.RouteType;
import com.flowmind.dispatch.runtime.ConversationLogEntry;
import com.flowmind.dispatch.runtime.DispatchTrace;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class DispatchTelemetryService {
  private static final int MAX_TRACE_LIMIT = 100;
  private static final int DEFAULT_METRICS_WINDOW = 50;
  private static final int MAX_METRICS_WINDOW = 200;

  private final List<DispatchTrace> traces = new ArrayList<>();
  private final List<ConversationLogEntry> conversations = new ArrayList<>();
  private final AtomicInteger total = new AtomicInteger();
  private final AtomicInteger fallback = new AtomicInteger();
  private final AtomicInteger lowConfidence = new AtomicInteger();
  private final EnumMap<FallbackReason, AtomicInteger> fallbackReasonCounts =
      new EnumMap<>(FallbackReason.class);
  private long latencyTotalMs = 0L;

  public DispatchTelemetryService() {
    for (FallbackReason reason : FallbackReason.values()) {
      fallbackReasonCounts.put(reason, new AtomicInteger());
    }
  }

  public synchronized void saveTrace(DispatchTrace trace) {
    traces.add(trace);
  }

  public synchronized void saveConversation(ConversationLogEntry entry) {
    conversations.add(entry);
  }

  public synchronized void recordMetrics(
      RouteType route, FallbackReason fallbackReason, Duration latency) {
    total.incrementAndGet();
    latencyTotalMs += latency.toMillis();
    if (route == RouteType.LLM) {
      fallback.incrementAndGet();
    }
    if (fallbackReason == FallbackReason.LOW_CONFIDENCE) {
      lowConfidence.incrementAndGet();
    }
    if (fallbackReason != null) {
      fallbackReasonCounts.get(fallbackReason).incrementAndGet();
    }
  }

  public synchronized MetricsSnapshot snapshot(int window) {
    int totalCount = total.get();
    double fallbackRate = totalCount == 0 ? 0.0 : (double) fallback.get() / totalCount;
    double misclassificationRate =
        totalCount == 0 ? 0.0 : (double) lowConfidence.get() / totalCount;
    double avgLatency = totalCount == 0 ? 0.0 : (double) latencyTotalMs / totalCount;

    int safeWindow = Math.min(Math.max(window, 1), MAX_METRICS_WINDOW);
    int recentFrom = Math.max(traces.size() - safeWindow, 0);
    List<DispatchTrace> recent = traces.subList(recentFrom, traces.size());
    int recentFallbackCount =
        (int) recent.stream().filter(trace -> trace.route() == RouteType.LLM).count();
    double recentFallbackRate =
        recent.isEmpty() ? 0.0 : (double) recentFallbackCount / recent.size();
    double recentFallbackLatencyAvgMs =
        recentFallbackCount == 0
            ? 0.0
            : recent.stream()
                .filter(trace -> trace.route() == RouteType.LLM)
                .mapToLong(trace -> trace.latencyMs() == null ? 0L : trace.latencyMs())
                .average()
                .orElse(0.0);
    Map<String, Integer> recentCounts = recentFallbackReasonSnapshot(recent);
    Map<String, Double> recentRates = recentFallbackReasonRateSnapshot(recentCounts, recent.size());
    Map<String, Double> recentRatesWithinFallback =
        recentFallbackReasonRateWithinFallbackSnapshot(recentCounts);

    return new MetricsSnapshot(
        totalCount,
        fallbackRate,
        misclassificationRate,
        avgLatency,
        fallbackReasonSnapshot(),
        fallbackReasonRateSnapshot(),
        fallbackReasonRateWithinFallbackSnapshot(),
        safeWindow,
        recent.size(),
        recentFallbackCount,
        recentFallbackRate,
        recentFallbackLatencyAvgMs,
        recentCounts,
        recentRates,
        recentRatesWithinFallback);
  }

  public synchronized MetricsSnapshot snapshot() {
    return snapshot(DEFAULT_METRICS_WINDOW);
  }

  public synchronized List<DispatchTrace> recentTraces(int limit) {
    int safeLimit = Math.min(Math.max(limit, 1), MAX_TRACE_LIMIT);
    int fromIndex = Math.max(traces.size() - safeLimit, 0);
    return new ArrayList<>(traces.subList(fromIndex, traces.size()));
  }

  private Map<String, Integer> fallbackReasonSnapshot() {
    Map<String, Integer> snapshot = new java.util.LinkedHashMap<>();
    for (FallbackReason reason : FallbackReason.values()) {
      snapshot.put(reason.name(), fallbackReasonCounts.get(reason).get());
    }
    return snapshot;
  }

  private Map<String, Double> fallbackReasonRateSnapshot() {
    Map<String, Double> snapshot = new java.util.LinkedHashMap<>();
    int totalCount = total.get();
    for (FallbackReason reason : FallbackReason.values()) {
      double rate =
          totalCount == 0 ? 0.0 : (double) fallbackReasonCounts.get(reason).get() / totalCount;
      snapshot.put(reason.name(), rate);
    }
    return snapshot;
  }

  private Map<String, Double> fallbackReasonRateWithinFallbackSnapshot() {
    Map<String, Double> snapshot = new java.util.LinkedHashMap<>();
    int fallbackCount = fallback.get();
    for (FallbackReason reason : FallbackReason.values()) {
      double rate =
          fallbackCount == 0
              ? 0.0
              : (double) fallbackReasonCounts.get(reason).get() / fallbackCount;
      snapshot.put(reason.name(), rate);
    }
    return snapshot;
  }

  private Map<String, Integer> recentFallbackReasonSnapshot(List<DispatchTrace> recent) {
    Map<String, Integer> snapshot = new java.util.LinkedHashMap<>();
    for (FallbackReason reason : FallbackReason.values()) {
      snapshot.put(reason.name(), 0);
    }
    for (DispatchTrace trace : recent) {
      for (FallbackReason reason : FallbackReason.values()) {
        if (reason.name().equals(trace.reason())) {
          snapshot.put(reason.name(), snapshot.get(reason.name()) + 1);
        }
      }
    }
    return snapshot;
  }

  private Map<String, Double> recentFallbackReasonRateSnapshot(
      Map<String, Integer> recentCounts, int recentSize) {
    Map<String, Double> snapshot = new java.util.LinkedHashMap<>();
    for (FallbackReason reason : FallbackReason.values()) {
      int count = recentCounts.get(reason.name());
      double rate = recentSize == 0 ? 0.0 : (double) count / recentSize;
      snapshot.put(reason.name(), rate);
    }
    return snapshot;
  }

  private Map<String, Double> recentFallbackReasonRateWithinFallbackSnapshot(
      Map<String, Integer> recentCounts) {
    Map<String, Double> snapshot = new java.util.LinkedHashMap<>();
    int fallbackCount = recentCounts.values().stream().mapToInt(Integer::intValue).sum();
    for (FallbackReason reason : FallbackReason.values()) {
      int count = recentCounts.get(reason.name());
      double rate = fallbackCount == 0 ? 0.0 : (double) count / fallbackCount;
      snapshot.put(reason.name(), rate);
    }
    return snapshot;
  }

  public record MetricsSnapshot(
      int totalRequests,
      double fallbackRate,
      double misclassificationRate,
      double averageLatencyMs,
      Map<String, Integer> fallbackReasonCounts,
      Map<String, Double> fallbackReasonRates,
      Map<String, Double> fallbackReasonRatesWithinFallback,
      int metricsWindow,
      int recentRequestCount,
      int recentFallbackCount,
      double recentFallbackRate,
      double recentFallbackLatencyAvgMs,
      Map<String, Integer> recentFallbackReasonCounts,
      Map<String, Double> recentFallbackReasonRates,
      Map<String, Double> recentFallbackReasonRatesWithinFallback) {}
}
