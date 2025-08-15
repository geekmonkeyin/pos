package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepo extends MongoRepository<State, String> {
}
