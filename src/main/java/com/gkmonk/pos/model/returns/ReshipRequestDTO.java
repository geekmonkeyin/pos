package com.gkmonk.pos.model.returns;

import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.List;

@Data
public class ReshipRequestDTO {
    @NotBlank
    private String returnId;
    @NotBlank
    private String orderNo;
    private String note;

    private List<Item> items;

    @Data
    public static class Item {
        @NotBlank private String productId;
        @NotBlank private String variantId;
        private String productName;               // optional but useful
        private int qty;
    }
}
