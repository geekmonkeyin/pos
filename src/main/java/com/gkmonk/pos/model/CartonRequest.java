package com.gkmonk.pos.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("carton_details")
@Data
public class CartonRequest {
    private String image; // Base64 encoded image string
    private int carton;
    private String inboundId;
    private String productId;
    private double productCost;
    private String productName;
    private int quantity;
    @Id
    private String uniqueId;
}