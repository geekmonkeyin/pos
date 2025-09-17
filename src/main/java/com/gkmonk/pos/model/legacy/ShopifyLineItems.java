package com.gkmonk.pos.model.legacy;

import lombok.Data;

@Data
public class ShopifyLineItems {

	private String name;
	private String price;
	private String variant_id;
	private String sku;
	private String title;
	private Integer quantity;
	private String product_id;
	private String imageUrl;
	private LineProperties[] properties;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(name != null){
				builder.append(name+",");
		}
		if(sku != null){
			builder.append(sku+"\n");
		}
		return builder.toString();
	}
	
}
