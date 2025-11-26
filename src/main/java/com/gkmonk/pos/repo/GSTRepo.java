package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.GSTRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface GSTRepo extends MongoRepository<GSTRate, String> {
    @Query(value = "{ 'category' : ?0 }")
    GSTRate findFirstByCategory(String category);
}
