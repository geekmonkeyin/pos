package com.gkmonk.pos.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gst_rates")
public class GSTRate {

    private String category;
    private String hsn;
    private double rate;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}
