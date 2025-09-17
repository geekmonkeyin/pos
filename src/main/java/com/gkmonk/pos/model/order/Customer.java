package com.gkmonk.pos.model.order;

import lombok.Data;

@Data
public class Customer {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String pincode;
}
