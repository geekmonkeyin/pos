package com.gkmonk.pos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payment_receipts")
public class PaymentReceipts {

    @Id
    private String id;
    private String paymentTo;
    private String vendorName;
    private Double amount;
    private String date;
    private String whatsappNumber;
    private String imageId; // Reference to the uploaded image
    private String remarks;
    private String invoiceId; // Optional field for invoice ID

    public PaymentReceipts(){

    }

    public PaymentReceipts(String paymentTo, String vendorName, Double amount, String date, String whatsappNumber,String remarks) {
        this.paymentTo = paymentTo;
        this.vendorName = vendorName;
        this.amount = amount;
        this.date = date;
        this.whatsappNumber = whatsappNumber;
        this.remarks = remarks;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentTo() {
        return paymentTo;
    }

    public void setPaymentTo(String paymentTo) {
        this.paymentTo = paymentTo;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
