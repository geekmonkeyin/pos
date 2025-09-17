package com.gkmonk.pos.model;

import lombok.Data;

@Data
public class BarcodeRequest {
    private String productId;
    private String productName;
    private int quantity;
    private int labelCount;

}