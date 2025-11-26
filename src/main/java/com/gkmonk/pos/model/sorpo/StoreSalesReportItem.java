package com.gkmonk.pos.model.sorpo;

import lombok.Data;

@Data
public class StoreSalesReportItem {
    private String productId;
    private String sku;
    private Integer quantity;
    private Double grossSaleValue; // inclusive of output GST
}

