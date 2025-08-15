package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.CartonRequest;
import com.gkmonk.pos.model.MaxCartonProjection;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductImagesRepository extends MongoRepository<CartonRequest, String> {


    @Aggregation(pipeline = {
            "{ '$match': { 'inboundId': ?0 } }",
            "{ '$group': { '_id': '$inboundId', 'maxCounterNo': { '$max': '$carton' } } }"
    })
    Optional<MaxCartonProjection> findByInboundId(String inboundId);

    @Query("{ 'inboundId': ?0 }")
    Optional<List<CartonRequest>> findCartonsByInbound(String inboundId);
}