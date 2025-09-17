package com.gkmonk.pos.model.purchase;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("purchase_order")
@Data
public class PurchaseOrder {
    @Id
    private String orderId;
    private String vendorName;
    private String orderDate;
    private Double totalAmount;

}
