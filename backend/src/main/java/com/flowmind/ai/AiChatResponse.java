package com.flowmind.ai;

public record AiChatResponse(
    String status,
    String answer,
    String provider,
    String model,
    long latencyMs,
    String sessionId,
    String requestId) {}
