package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.outbound.OutboundOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutboundRepo extends MongoRepository<OutboundOrder,Long> {
    @Query("{'awb': ?0}")
    Optional<OutboundOrder> findByAWB(String awb);
}
