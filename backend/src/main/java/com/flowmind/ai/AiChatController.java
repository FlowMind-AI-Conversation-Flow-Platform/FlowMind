package com.flowmind.ai;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

  private final AiChatService aiChatService;
  private final String chatModel;

  public AiChatController(
      AiChatService aiChatService,
      @Value("${spring.ai.ollama.chat.options.model:qwen2.5:7b}") String chatModel) {
    this.aiChatService = aiChatService;
    this.chatModel = chatModel;
  }

  @PostMapping("/chat")
  public ResponseEntity<?> chat(@RequestBody AiChatRequest request) {
    Instant start = Instant.now();
    String requestId = UUID.randomUUID().toString();
    String message = request == null ? null : request.message();
    String sessionId = request == null ? null : request.sessionId();
    if (message == null || message.isBlank()) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "requestId",
                  requestId,
                  "errorCode",
                  "INVALID_REQUEST",
                  "error",
                  "message is required",
                  "timestamp",
                  Instant.now().toString()));
    }

    try {
      String answer = aiChatService.chat(message);
      long latencyMs = Duration.between(start, Instant.now()).toMillis();
      return ResponseEntity.ok(
          new AiChatResponse(answer, "ollama", chatModel, latencyMs, sessionId, requestId));
    } catch (RuntimeException ex) {
      return ResponseEntity.status(503)
          .body(
              Map.of(
                  "requestId",
                  requestId,
                  "errorCode",
                  "LLM_UNAVAILABLE",
                  "error",
                  "llm_unavailable",
                  "detail",
                  ex.getMessage(),
                  "timestamp",
                  Instant.now().toString()));
    }
  }
}
