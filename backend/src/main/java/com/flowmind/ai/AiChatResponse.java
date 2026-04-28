package com.flowmind.ai;

public record AiChatResponse(String answer, String provider, long latencyMs) {}
