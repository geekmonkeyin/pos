package com.gkmonk.pos.model.order;

import com.gkmonk.pos.model.legacy.OrderSourceType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Document("orders")
@Data
public class Orders {

    @Id
    private String id;           // Mongo _id
    private String orderNo;          // "#100045"
    private OrderStatus status;      // PICKED / PACKED
    private LocalDate orderDate;
    private BigDecimal total;
    private String lastCourier;      // "BlueDart" etc.
    private String note;
    private boolean gift;
    private Customer customer;
    private List<LineItem> items;
    private int pastOrdersCount;
    private OrderSourceType orderSourceType;
}
