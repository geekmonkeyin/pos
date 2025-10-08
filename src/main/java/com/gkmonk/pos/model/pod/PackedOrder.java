package com.gkmonk.pos.model.pod;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("orders_reports")
@Data
public class PackedOrder {

    //orderid
    private String orderId;
    //awb
    private String awb;
    //courier company
    private String courierCompany;
    //customStatus
    private String customStatus;
    //gmId
    private String gmId;
    //customer name
    private String customerName;

    private double totalAmount;
    private String paymentMode;
    private CustomerInfo customerInfo;
    private List<ProductDetails> productDetails;
    private double weight;
    private boolean cod;
    private String orderStatusUrl;


    public PackedOrder() {
    }

    public PackedOrder(String orderId, String awb, String courierCompany, String customStatus, String gmId, String customerName) {
        this.orderId = orderId;
        this.awb = awb;
        this.courierCompany = courierCompany;
        this.customStatus = customStatus;
        this.gmId = gmId;
        this.customerName = customerName;
    }

}
