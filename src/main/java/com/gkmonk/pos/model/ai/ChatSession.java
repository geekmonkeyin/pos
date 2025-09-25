package com.gkmonk.pos.model.ai;

// model/ChatSession.java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document("chat_sessions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatSession {
    @Id private String id;
    private String sessionId;
    private String lastChoiceId;
    private Instant createdAt;
    private Instant updatedAt;
    @Builder.Default private Map<String,Object> context = new HashMap<>();
}

