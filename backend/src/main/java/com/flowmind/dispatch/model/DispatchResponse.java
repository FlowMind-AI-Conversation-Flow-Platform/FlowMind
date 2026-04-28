package com.flowmind.dispatch.model;

import java.util.Map;

public record DispatchResponse(
    RouteType route,
    IntentType intent,
    String message,
    String nextAction,
    String fallbackReason,
    Map<String, String> slots) {}
