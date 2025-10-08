package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
class VerificationLine {
    @NotBlank
    private String lineId;

    @NotBlank
    private String sku;

    @Positive
    private int qty;

    @NotNull
    private LineMark marked;

    public VerificationLine() {}
    public VerificationLine(String lineId, String sku, int qty, LineMark marked) {
        this.lineId = lineId; this.sku = sku; this.qty = qty; this.marked = marked;
    }
}
