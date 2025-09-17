package com.gkmonk.pos.model.order;

import lombok.Data;
@Data
public class Item {
    private String sku;
    private String name;
    private int qty;
    private double weight;

}
