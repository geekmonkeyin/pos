package com.gkmonk.pos.model.sorpo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("store_inventory")
public class StoreInventory {
    @Id
    private String id;

    private String storeId;
    private String productId;

    private Integer openingStock;
    private Integer inwardStock;
    private Integer soldQty;
    private Integer closingStock;
}
