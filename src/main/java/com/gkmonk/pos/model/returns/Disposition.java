package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Disposition {
    @JsonProperty("RTO") RTO,
    @JsonProperty("Customer Return") CUSTOMER_RETURN,
    @JsonProperty("Cancelled") CANCELLED
}