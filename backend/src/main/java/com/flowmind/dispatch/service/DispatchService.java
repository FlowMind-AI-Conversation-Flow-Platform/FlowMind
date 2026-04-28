package com.flowmind.dispatch.service;

import com.flowmind.dispatch.model.DispatchRequest;
import com.flowmind.dispatch.model.DispatchResponse;
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

    private final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();

    public DispatchService(
            IntentClassifier intentClassifier,
            SlotService slotService,
            ScenarioExecutor scenarioExecutor,
            LlmGateway llmGateway,
            DispatchTelemetryService telemetryService
    ) {
        this.intentClassifier = intentClassifier;
        this.slotService = slotService;
        this.scenarioExecutor = scenarioExecutor;
        this.llmGateway = llmGateway;
        this.telemetryService = telemetryService;
    }

    public DispatchResponse dispatch(DispatchRequest request) {
        Instant start = Instant.now();
        String sessionId = request.sessionId() == null || request.sessionId().isBlank()
                ? "anonymous"
                : request.sessionId();

        SessionContext context = sessions.computeIfAbsent(sessionId, key -> new SessionContext());
        IntentClassifier.ClassificationResult classification = intentClassifier.classify(request.message());

        IntentType intent = classification.intent();
        double confidence = classification.confidence();

        if (context.activeIntent() != IntentType.UNKNOWN && intent == IntentType.UNKNOWN) {
            intent = context.activeIntent();
        } else {
            context.setActiveIntent(intent);
        }

        slotService.extractAndMerge(intent, request.message(), context);
        List<String> missingSlots = slotService.missingRequiredSlots(intent, context);

        String fallbackReason = fallbackReason(intent, confidence, request.message());
        DispatchResponse response;
        RouteType route;
        if (!missingSlots.isEmpty() && intent != IntentType.UNKNOWN) {
            route = RouteType.SCENARIO;
            response = new DispatchResponse(
                    route,
                    intent,
                    "추가 정보가 필요합니다: " + String.join(", ", missingSlots),
                    "CLARIFY",
                    null,
                    context.slots()
            );
        } else if (fallbackReason != null) {
            route = RouteType.LLM;
            response = llmGateway.fallback(intent, fallbackReason, context.slots());
        } else {
            route = RouteType.SCENARIO;
            response = scenarioExecutor.execute(intent, context);
        }

        DispatchTrace trace = new DispatchTrace(
                Instant.now(),
                sessionId,
                intent,
                confidence,
                route,
                fallbackReason == null ? "SCENARIO" : fallbackReason
        );
        telemetryService.saveTrace(trace);
        telemetryService.saveConversation(new ConversationLogEntry(
                Instant.now(),
                sessionId,
                request.message(),
                response.message(),
                route
        ));
        telemetryService.recordMetrics(route, fallbackReason, Duration.between(start, Instant.now()));

        return response;
    }

    public DispatchTelemetryService.MetricsSnapshot metrics() {
        return telemetryService.snapshot();
    }

    private String fallbackReason(IntentType intent, double confidence, String message) {
        String text = message == null ? "" : message.toLowerCase();
        if (confidence < 0.5 || intent == IntentType.UNKNOWN) {
            return "LOW_CONFIDENCE";
        }
        if (text.contains("그리고") || text.contains("및")) {
            return "COMPLEX_REQUEST";
        }
        if (text.contains("짜증") || text.contains("화나") || text.contains("불만")) {
            return "EMOTION_HEAVY";
        }
        return null;
    }
}
