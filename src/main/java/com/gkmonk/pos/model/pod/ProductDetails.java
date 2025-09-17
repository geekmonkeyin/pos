package com.gkmonk.pos.model.pod;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetails {

    //productId
    private String productId;
    private String productName;
    private int quantity;
    private String imageURL;
    private double price;
    private String description;
    private List<String> keywords;

    //generate getter and setter

    @Override
    public String toString() {
        return "ProductDetails [productId=" + productId + ", productName=" + productName + ", quantity=" + quantity
                + ", imageURL=" + imageURL + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productId == null) ? 0 : productId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductDetails other = (ProductDetails) obj;
        if (productId == null) {
            if (other.productId != null)
                return false;
        } else if (!productId.equals(other.productId))
            return false;
        return true;
    }

}
