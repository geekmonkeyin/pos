package com.gkmonk.pos.model.pod;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("orders_reports")
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


    public boolean isCod() {
        return cod;
    }
    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }
    //generate getter and setter
    public String getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    //generate getter and setter
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
    public String getCustomStatus() {
        return customStatus;
    }
    public void setCustomStatus(String customStatus) {
        this.customStatus = customStatus;
    }
    public String getGmId() {
        return gmId;
    }
    public void setGmId(String gmId) {
        this.gmId = gmId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public List<ProductDetails> getProductDetails() {
        return productDetails;
    }
    public void setProductDetails(List<ProductDetails> productDetails) {
        this.productDetails = productDetails;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

}
