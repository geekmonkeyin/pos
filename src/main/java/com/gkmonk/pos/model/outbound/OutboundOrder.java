package com.gkmonk.pos.model.outbound;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("outbound_orders")
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

    public String getPickedUpBy() {
        return pickedUpBy;
    }
    public void setPickedUpBy(String pickedUpBy) {
        this.pickedUpBy = pickedUpBy;
    }
    public String getPhoneNo() {
        return phoneNo;
    }
    public String getManifestId() {
        return manifestId;
    }
    public void setManifestId(String manifestId) {
        this.manifestId = manifestId;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OutboundOrder() {
    }

    public OutboundOrder(String awb) {
        this.awb = awb;
    }


    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getAwb() {
        return awb;
    }
    public void setAwb(String awb) {
        this.awb = awb;
    }
    public String getCourierCompany() {
        return courierCompany;
    }
    public void setCourierCompany(String courierCompany) {
        this.courierCompany = courierCompany;
    }
    public String getPickupDate() {
        return pickupDate;
    }
    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }


    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
