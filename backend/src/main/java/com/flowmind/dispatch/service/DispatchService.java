package com.flowmind.dispatch.service;

import com.flowmind.config.FlowMindRuntimeProperties;
import com.flowmind.dispatch.model.DispatchRequest;
import com.flowmind.dispatch.model.DispatchResponse;
import com.flowmind.dispatch.model.FallbackReason;
import com.flowmind.dispatch.model.IntentType;
import com.flowmind.dispatch.model.RouteType;
import com.flowmind.dispatch.runtime.ConversationLogEntry;
import com.flowmind.dispatch.runtime.DispatchTrace;
import com.flowmind.dispatch.runtime.SessionContext;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {

  private final IntentClassifier intentClassifier;
  private final SlotService slotService;
  private final ScenarioExecutor scenarioExecutor;
  private final LlmGateway llmGateway;
  private final DispatchTelemetryService telemetryService;
  private final double confidenceThreshold;

  private final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();

  public DispatchService(
      FlowMindRuntimeProperties runtimeProperties,
      IntentClassifier intentClassifier,
      SlotService slotService,
      ScenarioExecutor scenarioExecutor,
      LlmGateway llmGateway,
      DispatchTelemetryService telemetryService) {
    this.intentClassifier = intentClassifier;
    this.slotService = slotService;
    this.scenarioExecutor = scenarioExecutor;
    this.llmGateway = llmGateway;
    this.telemetryService = telemetryService;
    this.confidenceThreshold = runtimeProperties.dispatch().confidenceThreshold();
  }

  public DispatchResponse dispatch(DispatchRequest request) {
    Instant start = Instant.now();
    String sessionId =
        request.sessionId() == null || request.sessionId().isBlank()
            ? "anonymous"
            : request.sessionId();

    SessionContext context = sessions.computeIfAbsent(sessionId, key -> new SessionContext());
    IntentClassifier.ClassificationResult classification =
        intentClassifier.classify(request.message());

    IntentType intent = classification.intent();
    double confidence = classification.confidence();

    if (context.activeIntent() != IntentType.UNKNOWN && intent == IntentType.UNKNOWN) {
      intent = context.activeIntent();
    } else {
      context.setActiveIntent(intent);
    }

    slotService.extractAndMerge(intent, request.message(), context);
    List<String> missingSlots = slotService.missingRequiredSlots(intent, context);

    FallbackReason fallbackReason = fallbackReason(intent, confidence, request.message());
    DispatchResponse response;
    RouteType route;
    if (fallbackReason != null) {
      route = RouteType.LLM;
      response = llmGateway.fallback(intent, fallbackReason, context.slots());
    } else if (!missingSlots.isEmpty() && intent != IntentType.UNKNOWN) {
      route = RouteType.SCENARIO;
      response =
          new DispatchResponse(
              route,
              intent,
              "추가 정보가 필요합니다: " + String.join(", ", missingSlots),
              "CLARIFY",
              null,
              context.slots());
    } else {
      route = RouteType.SCENARIO;
      response = scenarioExecutor.execute(intent, context);
    }

    Duration latency = Duration.between(start, Instant.now());
    DispatchTrace trace =
        new DispatchTrace(
            Instant.now(),
            sessionId,
            intent,
            confidence,
            route,
            fallbackReason == null ? "SCENARIO" : fallbackReason.name(),
            latency.toMillis());
    telemetryService.saveTrace(trace);
    telemetryService.saveConversation(
        new ConversationLogEntry(
            Instant.now(), sessionId, request.message(), response.message(), route));
    telemetryService.recordMetrics(route, fallbackReason, latency);

    return response;
  }

  public DispatchTelemetryService.MetricsSnapshot metrics(int window) {
    return telemetryService.snapshot(window);
  }

  public List<DispatchTrace> recentTraces(int limit) {
    return telemetryService.recentTraces(limit);
  }

  private FallbackReason fallbackReason(IntentType intent, double confidence, String message) {
    String text = message == null ? "" : message.toLowerCase();
    if (containsEmotionSignal(text)) {
      return FallbackReason.EMOTION_HEAVY;
    }
    if (containsComplexSignal(text)) {
      return FallbackReason.COMPLEX_REQUEST;
    }
    if (confidence < confidenceThreshold || intent == IntentType.UNKNOWN) {
      return FallbackReason.LOW_CONFIDENCE;
    }
    return null;
  }

  private boolean containsComplexSignal(String text) {
    return text.contains("그리고")
        || text.contains("및")
        || text.contains("또")
        || text.contains("동시에")
        || text.contains("같이");
  }

  private boolean containsEmotionSignal(String text) {
    return text.contains("짜증")
        || text.contains("화나")
        || text.contains("불만")
        || text.contains("화가")
        || text.contains("엉망");
  }
}
