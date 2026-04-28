package com.flowmind.dispatch.model;

public record DispatchRequest(
        String sessionId,
        String message
) {
}
