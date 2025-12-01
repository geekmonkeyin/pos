package com.gkmonk.pos.repo.sorpo;

import com.gkmonk.pos.model.sorpo.Outward;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface OutwardRepo extends MongoRepository<Outward, String> {

    @Query("{ '_id' : ?0 }")
    Optional<Outward> findByOutwardId(String outwardId);
}