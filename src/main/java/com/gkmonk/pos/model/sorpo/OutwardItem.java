package com.gkmonk.pos.model.sorpo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OutwardItem {

    private String productId;
    private String sku;
    private String productName;
    private BigDecimal mrp;
    private BigDecimal cost;
    private Integer qty;
    private String packagingType;

    // getters & setters
}

