package com.gkmonk.pos.model;

import java.time.LocalDateTime;
import java.util.List;


public class StockHistory {

    private Integer quantity;
    private String storage;
    private LocalDateTime updatedDate;
    private List<String> images;
    private String remarks;
    private String deviceName;

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

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }


    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Integer getShopifyStock() {
        return shopifyStock;
    }

    public void setShopifyStock(Integer shopifyStock) {
        this.shopifyStock = shopifyStock;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}