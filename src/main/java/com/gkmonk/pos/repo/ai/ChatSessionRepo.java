package com.gkmonk.pos.repo.ai;

// repo/ChatSessionRepo.java

import com.gkmonk.pos.model.ai.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatSessionRepo extends MongoRepository<ChatSession, String> {
    Optional<ChatSession> findBySessionId(String sessionId);
}
