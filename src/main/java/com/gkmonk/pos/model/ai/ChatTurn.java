package com.gkmonk.pos.model.ai;

// model/ChatTurn.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document("chat_turns")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatTurn {
    @Id private String id;
    private String sessionId;
    private Instant at;
    private String userChoiceId;
    private String botMessage;
    private Map<String,Object> contextSnapshot;
}

