package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LineMark {
    @JsonProperty("Goods Received") GOODS_RECEIVED,
    @JsonProperty("Damaged") DAMAGED
}

