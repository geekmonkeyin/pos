package com.gkmonk.pos.model.sorpo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document("store_sales_reports")
public class StoreSalesReport {
    @Id
    private String id;
    private String storeId;
    private String cycleMonth; // "2025-11"
    private String uploadedBy;
    private LocalDateTime uploadDate;

    private List<StoreSalesReportItem> items;
}