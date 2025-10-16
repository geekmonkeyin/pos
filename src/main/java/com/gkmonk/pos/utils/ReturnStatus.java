package com.gkmonk.pos.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReturnStatus {

    @JsonProperty("DRAFT") DRAFT,
    @JsonProperty("REFUNDED") REFUNDED,
    @JsonProperty("APPROVED") APPROVED,
    @JsonProperty("REJECTED") REJECTED,
    @JsonProperty("RESHIPPED") RESHIPPED,
    @JsonProperty("CANCELLED") CANCELLED


}
