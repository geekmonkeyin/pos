package com.gkmonk.pos.model.rule;

import lombok.Data;

@Data
public class Destination {

    public final String city;
    public final String state;
    public final String pincode;   // optional
    public final String address;   // optional
    public Destination(String city, String state, String pincode, String address) {
        this.city = city; this.state = state; this.pincode = pincode; this.address = address;
    }

}
