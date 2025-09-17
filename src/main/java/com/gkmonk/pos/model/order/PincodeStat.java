package com.gkmonk.pos.model.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pincode_stats")
public class PincodeStat {
    @Id
    private String pincode;
    private double avgDays;
    private CourierOption courierOption;
    // store average directly (or compute via aggregation)
    // getters/setters

}