package com.gkmonk.pos.model.ai;

import java.util.List;

public record QueryPlan(
        String collection,
        String operation, // "find" or "aggregate"
        List<CriterionSpec> criteria,       // for find
        List<SortSpec> sort,
        Integer limit,
        Integer skip,
        List<StageSpec> pipeline,           // for aggregate
        String explanation
) {
    public record CriterionSpec(String field, String op, Object value) {}
    public record SortSpec(String field, String dir) {}
    public record StageSpec(String stage, Object value) {} // e.g. {"stage":"$match","value":{...}}
}

