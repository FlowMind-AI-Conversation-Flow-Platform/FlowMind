package com.flowmind.dispatch;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void accountInquiryShouldRequestMissingSlotsFirst() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"s1","message":"계좌 확인하고 싶어요"}
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.intent").value("ACCOUNT_INFO_INQUIRY"))
        .andExpect(jsonPath("$.nextAction").value("CLARIFY"));
  }

  @Test
  void transactionInquiryShouldCompleteAfterSlotsProvided() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"s2","message":"계좌 지난달 이체 거래내역"}
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.intent").value("TRANSACTION_HISTORY_INQUIRY"))
        .andExpect(jsonPath("$.route").value("SCENARIO"))
        .andExpect(jsonPath("$.nextAction").value("TRANSACTION_HISTORY"));
  }

  @Test
  void emotionHeavyShouldRouteToFallback() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"s3","message":"정말 화나고 불만인데 왜 이렇게 엉망이야"}
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.route").value("LLM"))
        .andExpect(jsonPath("$.fallbackReason").value("EMOTION_HEAVY"));
  }

  @Test
  void complexRequestShouldRouteToFallback() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"s4","message":"계좌 정보랑 거래내역을 동시에 그리고 같이 확인해줘"}
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.route").value("LLM"))
        .andExpect(jsonPath("$.fallbackReason").value("COMPLEX_REQUEST"));
  }

  @Test
  void lowConfidenceShouldRouteToFallback() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"s5","message":"아무튼 뭔가 좀 해줘"}
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.route").value("LLM"))
        .andExpect(jsonPath("$.fallbackReason").value("LOW_CONFIDENCE"));
  }

  @Test
  void metricsEndpointShouldReturnSnapshot() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"m1","message":"아무튼 뭔가 좀 해줘"}
                                """))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/dispatch/metrics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalRequests").exists())
        .andExpect(jsonPath("$.fallbackRate").exists())
        .andExpect(jsonPath("$.averageLatencyMs").exists())
        .andExpect(jsonPath("$.fallbackReasonCounts.LOW_CONFIDENCE").exists())
        .andExpect(jsonPath("$.fallbackReasonCounts.COMPLEX_REQUEST").exists())
        .andExpect(jsonPath("$.fallbackReasonCounts.EMOTION_HEAVY").exists());
  }

  @Test
  void tracesEndpointShouldReturnRecentItems() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"t1","message":"계좌 확인하고 싶어요"}
                                """))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/dispatch/traces").param("limit", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sessionId").value("t1"))
        .andExpect(jsonPath("$[0].latencyMs").exists())
        .andExpect(jsonPath("$[0].reason").exists());
  }

  @Test
  void tracesEndpointShouldHandleInvalidAndLargeLimitSafely() throws Exception {
    mockMvc
        .perform(
            post("/api/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {"sessionId":"t2","message":"아무튼 뭔가 좀 해줘"}
                                """))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/dispatch/traces").param("limit", "0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sessionId").exists());

    mockMvc.perform(get("/api/dispatch/traces").param("limit", "9999")).andExpect(status().isOk());
  }
}
