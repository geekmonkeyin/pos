package com.gkmonk.pos.model.sorpo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("stores")
public class Store {
    @Id
    private String id;
    private String name;
    private Double takeRate;          // e.g. 15 (%)
    private Double outputGstRate;     // GST on sale (5 / 12 / 18)
    private Double settlementGstRate; // GST on basic value
    private String gstNo;
    private String paymentCycle;      // "MONTHLY", "WEEKLY"
    private String address;
    private Double rental;
    private String agreementId;
    private String agreementFileName;      // original filename
    private String agreementContentType;   // usually application/pdf

}