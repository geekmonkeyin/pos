package com.gkmonk.pos.repo.ai;

import com.gkmonk.pos.model.ai.ChatOption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatOptionRepo extends MongoRepository<ChatOption, String> {
    Optional<ChatOption> findByOptionId(String optionId);
    List<ChatOption> findByRootTrue();
    List<ChatOption> findByOptionIdIn(List<String> ids);
}