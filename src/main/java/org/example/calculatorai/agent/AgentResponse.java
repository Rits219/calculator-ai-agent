package org.example.calculatorai.agent;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentResponse {
    private final String intent;
    private final boolean supported;
    private final Integer result;
    private final String message;

    public AgentResponse(
            String intent,
            boolean supported,
            Integer result,
            String message) {
        this.intent = intent;
        this.supported = supported;
        this.result = result;
        this.message = message;
    }

    @JsonProperty
    public String getIntent() {
        return intent;
    }

    @JsonProperty
    public boolean isSupported() {
        return supported;
    }

    @JsonProperty
    public Integer getResult() {
        return result;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }
}
