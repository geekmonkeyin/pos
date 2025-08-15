package com.gkmonk.pos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "inventory_shopify")
public class Inventory {

    @Id
    private String upcId;

    private LocalDate inboundDate;

    private LocalDate updatedDate;

    @CSVAnnotations(column = "product_title")
    private String productTitle;

    @CSVAnnotations(column = "product_variant_title")
    private String productVariantTitle;

    @CSVAnnotations(column = "product_variant_sku")
    private String productVariantSku;

    @CSVAnnotations(column = "cost")
    private double cost;

    @CSVAnnotations(column = "product_id")
    private String productId;

    @CSVAnnotations(column = "product_type")
    private String productType;


    @CSVAnnotations(column = "product_variant_id")
    private String productVariantId;

    @CSVAnnotations(column = "last_cost_recorded")
    private String lastCostRecorded;

    @CSVAnnotations(column = "ending_quantity")
    private int endingQuantity;

    @CSVAnnotations(column = "sum_last_total_inventory_value")
    private double sumLastTotalInventoryValue;

    @CSVAnnotations(column = "storage")
    private String storage;

    @CSVAnnotations(column = "quantity")
    private Integer quantity;

    private Integer shopifyQuantity;

    private boolean isElectronics;

    private String electronicsType;

    private List<byte[]> resources;


    private List<String> images;

    private boolean temporarayInventory;

    private String imageUrl;

    private List<StockHistory> stockHistory;

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isElectronics() {
        return isElectronics;
    }

    public void setElectronics(boolean electronics) {
        isElectronics = electronics;
    }

    public String getElectronicsType() {
        return electronicsType;
    }

    public void setElectronicsType(String electronicsType) {
        this.electronicsType = electronicsType;
    }


    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductVariantTitle() {
        return productVariantTitle;
    }

    public void setProductVariantTitle(String productVariantTitle) {
        this.productVariantTitle = productVariantTitle;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }

    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(String productVariantId) {
        this.productVariantId = productVariantId;
    }

    public String getLastCostRecorded() {
        return lastCostRecorded;
    }

    public void setLastCostRecorded(String lastCostRecorded) {
        this.lastCostRecorded = lastCostRecorded;
    }

    public int getEndingQuantity() {
        return endingQuantity;
    }

    public void setEndingQuantity(int endingQuantity) {
        this.endingQuantity = endingQuantity;
    }

    public double getSumLastTotalInventoryValue() {
        return sumLastTotalInventoryValue;
    }

    public void setSumLastTotalInventoryValue(double sumLastTotalInventoryValue) {
        this.sumLastTotalInventoryValue = sumLastTotalInventoryValue;
    }

    public LocalDate getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(LocalDate inboundDate) {
        this.inboundDate = inboundDate;
    }

    public List<String> getImages() {
        if( images == null){
            this.images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setTemporarayInventory(boolean flag) {
        this.temporarayInventory = flag;
    }

    public boolean isTemporarayInventory() {
        return temporarayInventory;
    }

    public List<byte[]> getResources() {
        return resources == null ? new ArrayList<>() : resources;
    }

    public void setResources(List<byte[]> resources) {
        this.resources = resources;
    }

    public String getUpcId() {
        return upcId;
    }

    public void setUpcId(String upcId) {
        this.upcId = upcId;
    }

    public Integer getShopifyQuantity() {
        return shopifyQuantity;
    }

    public void setShopifyQuantity(Integer shopifyQuantity) {
        this.shopifyQuantity = shopifyQuantity;
    }

    public List<StockHistory> getStockHistory() {
        if(stockHistory == null){
            stockHistory = new ArrayList<>();
        }
        return stockHistory;
    }

    public void setStockHistory(List<StockHistory> stockHistory) {
        this.stockHistory = stockHistory;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }


    @JsonIgnore
    public String getFormattedLastUpdated() {
        return this.updatedDate != null ? this.updatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "1900-01-01";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
