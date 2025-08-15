package com.gkmonk.pos.model.returns;

import com.gkmonk.pos.model.pod.PackedOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("return_order")
public class ReturnOrder {

    @Id
    private String orderId;
    private PackedOrder packedOrder;
    private String labelLink;

    public ReturnOrder() {
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public PackedOrder getPackedOrder() {
        return packedOrder;
    }
    public void setPackedOrder(PackedOrder packedOrder) {
        this.packedOrder = packedOrder;
    }
    public String getLabelLink() {
        return labelLink;
    }
    public void setLabelLink(String labelLink) {
        this.labelLink = labelLink;

    }
}
