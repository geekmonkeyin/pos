package com.gkmonk.pos.repo.purchaseorder;

import com.gkmonk.pos.model.purchase.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepo extends MongoRepository<PurchaseOrder,Long> {

    @Query(value = "{$and:[ {'vendorName' : ?0},{'orderDate' : { $gte: ?1, $lte: ?2 }}] }",sort = "{orderDate:-1,vendor:-1}")
    Optional<List<PurchaseOrder>> findFilteredReceipts(String vendorName, String startDate, String endDate);

    @Query(value = "{$and:[ {'orderDate' : { $gte: ?0, $lte: ?1 }}] }",sort = "{orderDate:-1,vendor:-1}")
    Optional<List<PurchaseOrder>> findFilteredReceipts(String startDate, String endDate);
}
