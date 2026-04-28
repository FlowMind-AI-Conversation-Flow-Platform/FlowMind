package com.flowmind.ai;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

  private final AiChatService aiChatService;

  public AiChatController(AiChatService aiChatService) {
    this.aiChatService = aiChatService;
  }

  @PostMapping("/chat")
  public ResponseEntity<?> chat(@RequestBody AiChatRequest request) {
    Instant start = Instant.now();
    String message = request == null ? null : request.message();
    if (message == null || message.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "message is required"));
    }

    try {
      String answer = aiChatService.chat(message);
      long latencyMs = Duration.between(start, Instant.now()).toMillis();
      return ResponseEntity.ok(new AiChatResponse(answer, "ollama", latencyMs));
    } catch (RuntimeException ex) {
      return ResponseEntity.status(503)
          .body(Map.of("error", "llm_unavailable", "detail", ex.getMessage()));
    }
  }
}
