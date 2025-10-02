package com.gkmonk.pos.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class StockHistory {

    private Integer quantity;
    private String storage;
    private LocalDateTime updatedDate;
    private List<String> images;
    private String remarks;
    private String deviceName;
    private String empId;
    private Integer shopifyStock;

    public StockHistory(){
    }

    public StockHistory(Integer quantity,Integer shopifyStock, String storage, List<String> images, LocalDateTime updatedDate) {
        this.quantity = quantity;
        this.storage = storage;
        this.updatedDate = updatedDate;
        this.images = images;
        this.shopifyStock = shopifyStock;
    }

}