package com.gkmonk.pos.repo.rules;

import com.gkmonk.pos.rules.model.CourierRules;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface DBRuleRepo extends MongoRepository<CourierRules,String> {
    @Query("{ '_id' : ?0 }")
    Optional<CourierRules> findRuleById(String ruleName);

}
