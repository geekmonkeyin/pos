package com.gkmonk.pos.repo.returns;

import com.gkmonk.pos.model.returns.ReturnOrder;
import com.gkmonk.pos.model.returns.ReturnVerificationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnVerificationRepo extends MongoRepository<ReturnVerificationRequest, String> {


    @Query( "{ 'status' : ?0 }")
    List<ReturnVerificationRequest> findByStatus(String name);
}
