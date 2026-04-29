package com.flowmind.ai;

public record AiChatResponse(
    String answer, String provider, String model, long latencyMs, String sessionId) {}
