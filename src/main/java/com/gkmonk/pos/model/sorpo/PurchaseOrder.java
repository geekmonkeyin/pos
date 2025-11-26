package com.gkmonk.pos.model.sorpo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document("purchase_orders")
public class PurchaseOrder {
    @Id
    private String id;

    private String poNo;
    private String storeId;
    private String cycleMonth;

    private Double grossSales;
    private Double outputGst;
    private Double takeRateValue;
    private Double basicValue;
    private Double settlementGst;
    private Double finalValue;

    private String status; // DRAFT, APPROVED, SENT, PAID
    private LocalDate createdDate;
}
