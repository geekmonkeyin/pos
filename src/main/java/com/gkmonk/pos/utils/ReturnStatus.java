package com.gkmonk.pos.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReturnStatus {

    @JsonProperty("DRAFT") DRAFT,
    @JsonProperty("REFUNDED") REFUNDED,
    @JsonProperty("RESHIPPED") RESHIPPED,
    @JsonProperty("CANCELLED") CANCELLED


}
