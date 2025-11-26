package com.gkmonk.pos.model.sorpo;

import lombok.Data;

@Data
public class PurchaseOrderItem {
    private String productId;
    private String sku;
    private Integer quantity;
    private Double grossSale;
    private Double outputGst;
    private Double takeRateValue;
    private Double basicValue;
}