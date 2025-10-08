package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.checkerframework.checker.index.qual.Positive;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderLine {
    @NotBlank
    private String id;
    private String variantId;
    private String imageURL;
    private String orderURL;

    @NotBlank
    private String title;

    @NotBlank
    private String sku;

    @Positive
    private int qty;

    public OrderLine() {}
    public OrderLine(String id, String title, String sku, int qty) {
        this.id = id; this.title = title; this.sku = sku; this.qty = qty;
    }

}