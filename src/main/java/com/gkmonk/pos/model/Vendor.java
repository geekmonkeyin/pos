package com.gkmonk.pos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("vendors")
public class Vendor {
    @Id
    private String vendorId;
    private String vendorName;
    private String vendorAddress;
    private String vendorContact;
    private String vendorEmail;
    private Double vendorBalance;

    public Vendor() {
    }

    public Vendor(String vendorId, String vendorName, String vendorAddress, String vendorContact, String vendorEmail) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.vendorContact = vendorContact;
        this.vendorEmail = vendorEmail;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorContact() {
        return vendorContact;
    }

    public void setVendorContact(String vendorContact) {
        this.vendorContact = vendorContact;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public Double getVendorBalance() {
        return vendorBalance;
    }

    public void setVendorBalance(Double vendorBalance) {
        this.vendorBalance = vendorBalance;
    }
}
