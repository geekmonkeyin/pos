package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.GSTRate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GSTRepo extends MongoRepository<GSTRate, String> {
}
