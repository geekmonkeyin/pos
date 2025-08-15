package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepo extends MongoRepository<Vendor,String> {

    @Query(value = "{'vendorName': {$regex:?0,$options:'i'}}")
    List<Vendor> findByNameContainingIgnoreCase(String name);

}
