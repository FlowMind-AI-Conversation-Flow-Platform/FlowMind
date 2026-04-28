package com.flowmind.dispatch.service;

import com.flowmind.dispatch.model.DispatchResponse;
import com.flowmind.dispatch.model.IntentType;
import com.flowmind.dispatch.model.RouteType;
import com.flowmind.dispatch.runtime.SessionContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ScenarioExecutor {

  public DispatchResponse execute(IntentType intent, SessionContext sessionContext) {
    Map<String, String> slots = sessionContext.slots();
    return switch (intent) {
      case ACCOUNT_INFO_INQUIRY ->
          new DispatchResponse(
              RouteType.SCENARIO,
              intent,
              "본인확인이 완료되었습니다. 계좌 요약 정보를 안내드립니다.",
              "ACCOUNT_SUMMARY",
              null,
              slots);
      case TRANSACTION_HISTORY_INQUIRY ->
          new DispatchResponse(
              RouteType.SCENARIO,
              intent,
              "요청하신 기간의 거래내역을 조회했습니다.",
              "TRANSACTION_HISTORY",
              null,
              slots);
      case CARD_LOST_OR_LIMIT ->
          new DispatchResponse(
              RouteType.SCENARIO,
              intent,
              "요청 유형에 따라 처리했습니다. 추가 확인이 필요하면 상담사로 연결합니다.",
              "CARD_POLICY_PROCESS",
              null,
              slots);
      case UNKNOWN ->
          new DispatchResponse(
              RouteType.LLM, intent, "요청을 명확히 파악하지 못했습니다.", "FALLBACK", "UNKNOWN_INTENT", slots);
    };
  }
}
