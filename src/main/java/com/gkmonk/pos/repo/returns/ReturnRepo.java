package com.gkmonk.pos.repo.returns;

import com.gkmonk.pos.model.returns.ReturnOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepo extends MongoRepository<ReturnOrder, String> {
    // Additional query methods can be defined here if needed

}
