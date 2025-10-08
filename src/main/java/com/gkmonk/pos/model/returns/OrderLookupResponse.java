package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gkmonk.pos.model.order.Customer;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderLookupResponse {
    @NotBlank
    private String orderNo;

    @NotNull
    private Customer customer;

    private String orderURL;

    @NotNull
    private List<OrderLine> lines;

    // getters/setters/ctors
    public OrderLookupResponse() {}
    public OrderLookupResponse(String orderNo, Customer customer, List<OrderLine> lines,String ordeURL) {
        this.orderNo = orderNo;
        this.customer = customer;
        this.lines = lines;
        this.orderURL = ordeURL;
    }
}
