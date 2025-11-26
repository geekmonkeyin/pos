package com.gkmonk.pos.repo.sorpo;

import com.gkmonk.pos.model.sorpo.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {
    Optional<PurchaseOrder> findByStoreIdAndCycleMonth(String storeId, String cycleMonth);
}