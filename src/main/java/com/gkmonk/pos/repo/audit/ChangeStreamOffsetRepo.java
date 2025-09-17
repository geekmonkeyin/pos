package com.gkmonk.pos.repo.audit;

import com.gkmonk.pos.model.audit.ChangeStreamOffset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeStreamOffsetRepo extends MongoRepository<ChangeStreamOffset, String> {}
