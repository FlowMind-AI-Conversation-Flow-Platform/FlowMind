package com.flowmind.ai;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        .andExpect(jsonPath("$.error").value("message is required"));
  }

  @Test
  void shouldReturnAiAnswer() throws Exception {
    when(aiChatService.chat("안녕")).thenReturn("안녕하세요.");

    mockMvc
        .perform(
            post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"message":"안녕"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.provider").value("ollama"))
        .andExpect(jsonPath("$.answer").value("안녕하세요."));
  }
}
