package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.Inventory;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepo extends MongoRepository<Inventory,Long> {

    @Query("{ 'sku' : ?0 }")
    Inventory findBySku(String sku);
    @Query("{ 'temporarayInventory' : ?0}")
    Page<Inventory> findAllTempProducts(boolean status, Pageable pageable);

    @Query("{ 'temporarayInventory' : ?0}")
    List<Inventory> findAllTempProducts(boolean status);

    @Query("{ 'productTitle' : { $regex: ?0,$options:'i'} }")
    Optional<List<Inventory>> findAllByNameRegex(String query);

    @Query("{ '_id' : ?0 }")
    Optional<Inventory> findById(ObjectId upcId);

    @Query(value = "{ 'temporarayInventory' : false }", fields = "{ 'upcId' : 1 }")
    List<String> findUpdatedProductIds();

    @Query(value = "{ '_id' : ?0 }", delete = true)
    void deleteByUPCId(ObjectId id);

    @Query(value = "{$or:[ {'productId' : ?0 },{'productVariantId' : ?0 } ]}")
    Optional<List<Inventory>> findAllByProductId(String productId);

    @Query("{ $and: [{'shopifyQuantity': { $exists: true, $ne: null } },{ 'quantity': { $exists: true, $ne: null } },{ $expr: { $ne: ['$shopifyQuantity', '$quantity'] } }]}")
    Optional<List<Inventory>> findByMismatchedQty();

    @Query("{$or:[{_id: ?0},{productVariantId:?0}]}")
    Optional<List<Inventory>> findById(String id);

    @Query(value = "{ 'productId' : ?0 }", delete = true)
    void deleteByProductid(String productId);

    @Query(value = "{'storage':{'$exists':false},quantity:{$ne:0}}")
    Optional<List<Inventory>> findByNoStorage();
}
