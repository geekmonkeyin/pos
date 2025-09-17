package com.gkmonk.pos.model.packaging;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data @Builder
public class PackagingSummary {
    private long totalCount;
    private double avgVolumetricKg;
    private double avgActualKg;
    private double avgChargeableKg;

    // band counts in kg (e.g. "0-0.5", "0.5-1", "1-2", ...)
    @Builder.Default
    private Map<String, Long> weightBands = new LinkedHashMap<>();
}
