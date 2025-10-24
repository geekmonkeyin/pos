package com.gkmonk.pos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "inventory_shopify")
@Data
public class Inventory {

    @Id
    private String upcId;

    private String description;
    private String productGID;
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

    private double price;

    private List<String> images;

    private boolean temporarayInventory;

    private String imageUrl;

    private List<StockHistory> stockHistory;


    public List<String> getImages() {
        if( images == null){
            this.images = new ArrayList<>();
        }
        return images;
    }

    public List<byte[]> getResources() {
        return resources == null ? new ArrayList<>() : resources;
    }

    public List<StockHistory> getStockHistory() {
        if(stockHistory == null){
            stockHistory = new ArrayList<>();
        }
        return stockHistory;
    }

    @JsonIgnore
    public String getFormattedLastUpdated() {
        return this.updatedDate != null ? this.updatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "1900-01-01";
    }

}
