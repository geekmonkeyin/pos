package com.gkmonk.pos.repo.shopify;

import com.gkmonk.pos.model.legacy.ShopifyOrders;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopifyOrderRepo extends MongoRepository<ShopifyOrders, String> {
    @Query("{ 'customStatus':{$regex:?0,$options:'i'} }")
    Optional<List<ShopifyOrders>> findByStatus(String orderStatus);

    @Query("{ 'name': ?0 }")
    Optional<ShopifyOrders> findByName(String orderNo);
}
