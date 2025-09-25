package com.gkmonk.pos.model.ai;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_history")
@Data
public class ChatMessageLog {
    @Id
    private String id;
    private String userMessage;
    private String aiReply;
    private IntentType intentType;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String sessionID;
    private String choiceId;

}