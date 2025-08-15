package com.gkmonk.pos.model.ai;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_history")
public class ChatMessageLog {
    @Id
    private String id;
    private String userMessage;
    private String aiReply;
    private IntentType intentType;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String sessionID;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserMessage() {
        return userMessage;
    }
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    public String getAiReply() {
        return aiReply;
    }
    public void setAiReply(String aiReply) {
        this.aiReply = aiReply;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public IntentType getIntentType() {
        return intentType;
    }
    public void setIntentType(IntentType intentType) {
        this.intentType = intentType;
    }

    public String getSessionID() {
        return sessionID;
    }
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

}