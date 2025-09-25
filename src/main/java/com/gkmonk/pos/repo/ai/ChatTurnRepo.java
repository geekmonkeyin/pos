package com.gkmonk.pos.repo.ai;

// repo/ChatTurnRepo.java

import com.gkmonk.pos.model.ai.ChatTurn;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ChatTurnRepo extends MongoRepository<ChatTurn, String> {}
