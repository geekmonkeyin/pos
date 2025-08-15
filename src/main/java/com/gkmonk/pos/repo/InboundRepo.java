package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.InboundData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InboundRepo extends MongoRepository<InboundData,Long> {

    @Query("{'vendorName': ?0, 'status': ?1}")
    Optional<List<InboundData>> findByVendorNameAndStatus(String vendorName, String draft);

    @Query("{'vendorName': ?0}")
    Optional<List<InboundData>> findByVendorName(String vendorName);
}
