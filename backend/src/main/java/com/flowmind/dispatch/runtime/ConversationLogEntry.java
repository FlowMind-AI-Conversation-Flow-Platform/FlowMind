package com.flowmind.dispatch.runtime;

import com.flowmind.dispatch.model.RouteType;
import java.time.Instant;

public record ConversationLogEntry(
    Instant timestamp, String sessionId, String userMessage, String botMessage, RouteType route) {}
