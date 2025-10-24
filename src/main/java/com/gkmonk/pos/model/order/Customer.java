package com.gkmonk.pos.model.order;

import lombok.Data;

@Data
public class Customer {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String pincode;
    private String city;
    private String state;
    private String loyaltyId;
    private double totalSpent;
    private int ordersCount;

    public Customer() {
        this.totalSpent = 0.0;
        this.ordersCount = 0;
    }

}
