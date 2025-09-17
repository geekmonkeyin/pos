package com.gkmonk.pos.model;

import lombok.Data;

import java.util.List;

@Data
public class CartonDetails {

    private Integer cartonNo;
    private List<Inventory> productDetails;

}
