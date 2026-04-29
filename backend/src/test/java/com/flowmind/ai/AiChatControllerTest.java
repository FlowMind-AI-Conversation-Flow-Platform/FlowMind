package com.flowmind.ai;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AiChatControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AiChatService aiChatService;

  @Test
  void shouldReturnBadRequestWhenMessageIsBlank() throws Exception {
    mockMvc
        .perform(
            post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"message":" "}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.requestId").exists())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.error").value("message is required"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnAiAnswer() throws Exception {
    when(aiChatService.chat("안녕")).thenReturn("안녕하세요.");

    mockMvc
        .perform(
            post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"message":"안녕","sessionId":"s-1"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.provider").value("ollama"))
        .andExpect(jsonPath("$.model").exists())
        .andExpect(jsonPath("$.answer").value("안녕하세요."))
        .andExpect(jsonPath("$.latencyMs").exists())
        .andExpect(jsonPath("$.sessionId").value("s-1"))
        .andExpect(jsonPath("$.requestId").exists());
  }

  @Test
  void shouldReturnBadRequestWhenMessageTooLong() throws Exception {
    String longMessage = IntStream.range(0, 2001).mapToObj(i -> "a").collect(Collectors.joining());

    mockMvc
        .perform(
            post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"" + longMessage + "\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.requestId").exists())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.error").value("message length must be <= 2000"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldAllowNullSessionId() throws Exception {
    when(aiChatService.chat("테스트")).thenReturn("응답");

    mockMvc
        .perform(
            post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"message":"테스트"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.answer").value("응답"))
        .andExpect(jsonPath("$.sessionId").isEmpty())
        .andExpect(jsonPath("$.requestId").exists());
  }

  @Test
  void shouldReturnServiceUnavailableErrorCodeWhenLlmFails() throws Exception {
    when(aiChatService.chat("장애테스트")).thenThrow(new RuntimeException("connection refused"));

    mockMvc
        .perform(
            post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"message":"장애테스트","sessionId":"s-err"}
                    """))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.errorCode").value("LLM_UNAVAILABLE"))
        .andExpect(jsonPath("$.error").value("llm_unavailable"))
        .andExpect(jsonPath("$.detail").exists())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.requestId").exists());
  }
}
