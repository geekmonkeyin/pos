package com.gkmonk.pos.model.legacy;

import lombok.Data;

import java.util.List;

@Data
public class ShopifyFulfillment {

	private String id;
	private String status;
	private List<ShopifyLineItems> line_items;
	private String tracking_number;
	private String tracking_company;

}
