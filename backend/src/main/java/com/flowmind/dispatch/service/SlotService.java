package com.flowmind.dispatch.service;

import com.flowmind.dispatch.model.IntentType;
import com.flowmind.dispatch.runtime.SessionContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class SlotService {

    private static final Pattern DATE_BIRTH_PATTERN = Pattern.compile("(19|20)\\d{2}[./-]?\\d{2}[./-]?\\d{2}");
    private static final Pattern LAST4_PATTERN = Pattern.compile("\\b\\d{4}\\b");

    public void extractAndMerge(IntentType intent, String message, SessionContext sessionContext) {
        String text = message == null ? "" : message;
        Map<String, String> slots = sessionContext.slots();

        Matcher birthMatcher = DATE_BIRTH_PATTERN.matcher(text);
        if (birthMatcher.find()) {
            slots.putIfAbsent("birthDate", birthMatcher.group());
        }

        Matcher last4Matcher = LAST4_PATTERN.matcher(text);
        if (last4Matcher.find()) {
            slots.putIfAbsent("phoneLast4", last4Matcher.group());
        }

        if (text.contains("김") || text.contains("이") || text.contains("박")) {
            slots.putIfAbsent("name", "고객");
        }
        if (text.contains("지난달")) {
            slots.putIfAbsent("period", "LAST_MONTH");
        }
        if (text.contains("이번달")) {
            slots.putIfAbsent("period", "THIS_MONTH");
        }
        if (text.contains("이체")) {
            slots.putIfAbsent("transactionType", "TRANSFER");
        }
        if (text.contains("계좌")) {
            slots.putIfAbsent("accountId", "111-222-333333");
        }
        if (text.contains("입출금")) {
            slots.putIfAbsent("transactionType", "DEPOSIT_WITHDRAWAL");
        }
        if (text.contains("체크")) {
            slots.putIfAbsent("cardType", "CHECK");
        }
        if (text.contains("신용")) {
            slots.putIfAbsent("cardType", "CREDIT");
        }
        if (text.contains("분실")) {
            slots.putIfAbsent("requestType", "LOSS_BLOCK");
        }
        if (text.contains("한도")) {
            slots.putIfAbsent("requestType", "LIMIT_INQUIRY");
        }
        if (intent == IntentType.ACCOUNT_INFO_INQUIRY && text.contains("고객번호")) {
            slots.putIfAbsent("customerId", "CUST-001");
        }
    }

    public List<String> missingRequiredSlots(IntentType intent, SessionContext sessionContext) {
        Map<String, String> slots = sessionContext.slots();
        List<String> required = switch (intent) {
            case ACCOUNT_INFO_INQUIRY -> List.of("name", "birthDate", "phoneLast4");
            case TRANSACTION_HISTORY_INQUIRY -> List.of("accountId", "period", "transactionType");
            case CARD_LOST_OR_LIMIT -> List.of("cardType", "name", "requestType");
            case UNKNOWN -> List.of();
        };

        List<String> missing = new ArrayList<>();
        for (String requiredSlot : required) {
            if (!slots.containsKey(requiredSlot)) {
                missing.add(requiredSlot);
            }
        }
        return missing;
    }
}
