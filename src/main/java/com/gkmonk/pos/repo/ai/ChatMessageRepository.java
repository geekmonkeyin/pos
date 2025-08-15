package com.gkmonk.pos.repo.ai;

import com.gkmonk.pos.model.ai.ChatMessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageLog, String> {}
