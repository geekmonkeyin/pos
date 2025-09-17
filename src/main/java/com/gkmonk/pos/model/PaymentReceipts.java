package com.gkmonk.pos.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payment_receipts")
@Data
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

   }
