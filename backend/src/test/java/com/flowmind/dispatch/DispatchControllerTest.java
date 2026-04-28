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

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accountInquiryShouldRequestMissingSlotsFirst() throws Exception {
        mockMvc.perform(post("/api/dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sessionId":"s1","message":"계좌 확인하고 싶어요"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intent").value("ACCOUNT_INFO_INQUIRY"))
                .andExpect(jsonPath("$.nextAction").value("CLARIFY"));
    }

    @Test
    void transactionInquiryShouldCompleteAfterSlotsProvided() throws Exception {
        mockMvc.perform(post("/api/dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sessionId":"s2","message":"계좌 지난달 이체 거래내역"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intent").value("TRANSACTION_HISTORY_INQUIRY"))
                .andExpect(jsonPath("$.route").value("SCENARIO"))
                .andExpect(jsonPath("$.nextAction").value("TRANSACTION_HISTORY"));
    }

    @Test
    void emotionHeavyShouldRouteToFallback() throws Exception {
        mockMvc.perform(post("/api/dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sessionId":"s3","message":"정말 화나고 불만인데 왜 이렇게 엉망이야"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route").value("LLM"))
                .andExpect(jsonPath("$.fallbackReason").value("LOW_CONFIDENCE"));
    }

    @Test
    void metricsEndpointShouldReturnSnapshot() throws Exception {
        mockMvc.perform(get("/api/dispatch/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").exists())
                .andExpect(jsonPath("$.fallbackRate").exists())
                .andExpect(jsonPath("$.averageLatencyMs").exists());
    }
}
