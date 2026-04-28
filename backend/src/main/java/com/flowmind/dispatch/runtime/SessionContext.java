package com.flowmind.dispatch.runtime;

import com.flowmind.dispatch.model.IntentType;
import java.util.HashMap;
import java.util.Map;

public class SessionContext {

    private IntentType activeIntent = IntentType.UNKNOWN;
    private final Map<String, String> slots = new HashMap<>();

    public IntentType activeIntent() {
        return activeIntent;
    }

    public void setActiveIntent(IntentType activeIntent) {
        this.activeIntent = activeIntent;
    }

    public Map<String, String> slots() {
        return slots;
    }
}
