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

  public synchronized MetricsSnapshot snapshot() {
    int totalCount = total.get();
    double fallbackRate = totalCount == 0 ? 0.0 : (double) fallback.get() / totalCount;
    double misclassificationRate =
        totalCount == 0 ? 0.0 : (double) lowConfidence.get() / totalCount;
    double avgLatency = totalCount == 0 ? 0.0 : (double) latencyTotalMs / totalCount;
    return new MetricsSnapshot(
        totalCount, fallbackRate, misclassificationRate, avgLatency, fallbackReasonSnapshot());
  }

  public synchronized List<DispatchTrace> recentTraces(int limit) {
    int safeLimit = Math.max(limit, 1);
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

  public record MetricsSnapshot(
      int totalRequests,
      double fallbackRate,
      double misclassificationRate,
      double averageLatencyMs,
      Map<String, Integer> fallbackReasonCounts) {}
}
