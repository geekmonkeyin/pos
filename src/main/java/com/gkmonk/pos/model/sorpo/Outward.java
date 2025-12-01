package com.gkmonk.pos.model.sorpo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "outward")
public class Outward {

    @Id
    private String id;              // Mongo _id
    private String outwardId;       // business ID (also shown in UI)
    private String vendorId;
    private String vendorName;
    private LocalDate inwardDate;
    private String inwardLocation;
    private BigDecimal totalInventoryCost;
    private OutwardStatus status;   // DRAFT / SUBMITTED
    private Instant createdAt;
    private Instant updatedAt;
    private List<OutwardItem> items;

}
