package com.gkmonk.pos.model.outbound;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("outbound_orders")
@Data
public class OutboundOrder {
    @Id
    private String orderId;
    private String awb;
    private String courierCompany;
    private String pickupDate;
    private String customerName;
    private String manifestId;
    private String phoneNo;
    private String pickedUpBy;


    public OutboundOrder(String awb) {
        this.awb = awb;
    }
}
