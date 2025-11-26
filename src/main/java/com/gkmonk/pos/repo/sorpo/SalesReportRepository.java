package com.gkmonk.pos.repo.sorpo;

import com.gkmonk.pos.model.sorpo.StoreSalesReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SalesReportRepository extends MongoRepository<StoreSalesReport, String> {
    Optional<StoreSalesReport> findByStoreIdAndCycleMonth(String storeId, String cycleMonth);
}
