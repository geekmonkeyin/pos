package com.gkmonk.pos.model.legacy;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document("shopifyorders")
@Data
public class ShopifyOrders {

	private String id;
	private String contact_email;
	private String current_subtotal_price;
	private String current_total_tax;
	private String email;
	private String fulfillment_status;
	private String order_status_url;
	private String total_outstanding;
	private String total_price;
	private ShopifyAddress billing_address;
	private ShopifyAddress shipping_address;
	private ShopifyFulfillment[] fulfillments;
	private String[] payment_gateway_names;
	private OrderSourceType orderSourceType = OrderSourceType.SHOPIFY;
	private String created_at;
	private String customStatus;
	private String phone;
	private double weight;
	private boolean cod;
	private String name;
	private String note;
	private ShopifyCustomer customer;
	private ShopifyTaxLines[] tax_lines;
	private Map<String,String> imageListMap;
	private int delayed;
	private String pincode;
	private String deliveredDate;
	private CourierCompany courierCompany;
	private LocalDateTime packOrderDate;
	private double length;
	private double breadth;
	private double height;
	private double estimatedWeight;
	private double estimatedRate;
	private String estimatedDays;
	private String courierPreference;


	
	public LocalDateTime getPackOrderDate() {
		return packOrderDate != null ? packOrderDate : LocalDateTime.of(2000,1,1,0,0);
	}

}
