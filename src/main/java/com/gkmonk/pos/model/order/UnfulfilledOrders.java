package com.gkmonk.pos.model.order;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("unfulfilled_orders")
@Data
public class UnfulfilledOrders {
    private String id;
    private String status;
    private String date;
    private int total;
    private String courier;
    private String note;
    private boolean gift;
    private Customer customer;
    private List<Item> items;
    private int pastOrders;
}
