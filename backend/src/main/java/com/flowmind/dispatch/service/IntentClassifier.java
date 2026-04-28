package com.flowmind.dispatch.service;

import com.flowmind.dispatch.model.IntentType;
import org.springframework.stereotype.Component;

@Component
public class IntentClassifier {

  public ClassificationResult classify(String message) {
    String text = message == null ? "" : message.toLowerCase();

    if (containsAny(text, "분실", "한도", "카드 정지")) {
      return new ClassificationResult(IntentType.CARD_LOST_OR_LIMIT, 0.92);
    }
    if (containsAny(text, "거래내역", "이체", "입출금")) {
      return new ClassificationResult(IntentType.TRANSACTION_HISTORY_INQUIRY, 0.9);
    }
    if (containsAny(text, "계좌", "잔액", "본인확인")) {
      return new ClassificationResult(IntentType.ACCOUNT_INFO_INQUIRY, 0.88);
    }
    if (containsAny(text, "짜증", "화나", "불만", "엉망")) {
      return new ClassificationResult(IntentType.UNKNOWN, 0.35);
    }
    return new ClassificationResult(IntentType.UNKNOWN, 0.4);
  }

  private boolean containsAny(String text, String... keywords) {
    for (String keyword : keywords) {
      if (text.contains(keyword)) {
        return true;
      }
    }
    return false;
  }

  public record ClassificationResult(IntentType intent, double confidence) {}
}
