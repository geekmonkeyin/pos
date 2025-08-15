package com.gkmonk.pos.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("product_images")
public class ProductImages {

    private String productId;
    private String cartonNo;
    private byte[] image;
    private String inboundId;


    public ProductImages(){

    }
    public ProductImages(String productId,String cartonNo,byte[] image,String inboundId){
            this.image = image;
            this.cartonNo = cartonNo;
            this.productId = productId;
            this.inboundId = inboundId;


    }

    public String getCartonNo() {
        return cartonNo;
    }

    public void setCartonNo(String cartonNo) {
        this.cartonNo = cartonNo;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getInboundId() {
        return inboundId;
    }

    public void setInboundId(String inboundId) {
        this.inboundId = inboundId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }



}
