package com.flowmind.dispatch.service;

import com.flowmind.dispatch.model.DispatchResponse;
import com.flowmind.dispatch.model.IntentType;
import com.flowmind.dispatch.model.RouteType;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LlmGateway {

    public DispatchResponse fallback(IntentType intent, String fallbackReason, Map<String, String> slots) {
        return new DispatchResponse(
                RouteType.LLM,
                intent,
                "상황을 정확히 파악하기 위해 상담사를 연결하거나 추가 정보를 확인하겠습니다.",
                "LLM_FALLBACK",
                fallbackReason,
                slots
        );
    }
}
