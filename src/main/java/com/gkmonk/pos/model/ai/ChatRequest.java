package com.gkmonk.pos.model.ai;

import lombok.Data;

import java.util.Map;

@Data
public class ChatRequest {
    private String sessionId;
    private String message;   // when user types free-text
    private String choiceId;  // when user selects a pre-set option
    private Map<String,Object> context; // optional, to carry extra info

    public ChatRequest() {}

    public ChatRequest(String sessionId, String message, String choiceId, Map<String,Object> context) {
        this.sessionId = sessionId;
        this.message = message;
        this.choiceId = choiceId;
        this.context = context;
    }

}

