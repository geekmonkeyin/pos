package com.gkmonk.pos.model;

import java.util.List;

public class CartonDetails {

    private Integer cartonNo;
    private List<Inventory> productDetails;


    public Integer getCartonNo() {
        return cartonNo;
    }

    public void setCartonNo(Integer cartonNo) {
        this.cartonNo = cartonNo;
    }

    public List<Inventory> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<Inventory> productDetails) {
        this.productDetails = productDetails;
    }
}
