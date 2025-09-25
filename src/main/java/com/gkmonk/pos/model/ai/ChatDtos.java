package com.gkmonk.pos.model.ai;

// api/dto/ChatDtos.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class ChatDtos {
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class OptionDto { private String id; private String label; }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class OptionsResponse { private List<OptionDto> options; }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ChatRequest {
        private String choiceId;     // user clicked
        private String sessionId;    // widget session
        private Map<String,Object> context; // optional
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ChatResponse {
        private String message;      // bot message
        private List<OptionDto> options; // next options
    }
}
