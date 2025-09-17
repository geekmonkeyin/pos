package com.gkmonk.pos.model.packaging;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PackagingRow {
    private String id;
    private String orderNo;
    private Double length;
    private Double width;
    private Double height;
    private Double actualWeightKg;
    private Double volumetricWeightKg;
    private Double chargeableWeightKg;
    private String courier;
    private String warehouse;
    private String status;
    private String recommendedBoxCode;  // derived
}

