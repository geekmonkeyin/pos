package com.gkmonk.pos.model.counter;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("shopify_cursor")
public class ShopifyCursor {

    private String updatedDate;
    private String cursor;
    @Id
    private String store;

}
