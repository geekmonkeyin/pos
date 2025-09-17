package com.gkmonk.pos.model.legacy;

import lombok.Data;

@Data
public class ShopifyCustomer {
	
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private boolean accepts_marketing = true;
	private boolean verified_email = true;
	private String phone;
	private String password = "newpass";
	private String password_confirmation = "newpass";
	private int numberOfOrders;
	private double totalSpent;

}
