package com.gkmonk.pos.model.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("courier_options")
@Data
public class CourierOption {
    @Id
    private String id;
    private String name;     // "BlueDart"
    private String service;  // "Priority"
    private String eta;      // "1-3 days"
    private double cost;        // Rs
    private boolean cod;
    private String aggregator; // "Shiprocket"
    private String imageURL;


    // getters/setters
}