package com.gkmonk.pos.repo.orders;

import com.gkmonk.pos.model.pod.PackedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderReportRepo extends MongoRepository<PackedOrder,String> {
    @Query("{'awb': ?0}")
    Optional<List<PackedOrder>> findByAWB(String awb);
}
