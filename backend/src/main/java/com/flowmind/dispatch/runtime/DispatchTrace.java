package com.flowmind.dispatch.runtime;

import com.flowmind.dispatch.model.IntentType;
import com.flowmind.dispatch.model.RouteType;
import java.time.Instant;

public record DispatchTrace(
    Instant timestamp,
    String sessionId,
    IntentType intent,
    double confidence,
    RouteType route,
    String reason,
    Long latencyMs) {}
