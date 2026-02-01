package com.gkmonk.pos.model.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class QueryPlanSafetyValidator {

    private static final Set<String> ALLOWED_CRITERIA_OPS = Set.of(
            "eq","ne","gt","gte","lt","lte","in","nin","regex","exists","and","or"
    );

    private static final Set<String> BLOCKED_MONGO_STAGES = Set.of(
            "$where", "$function", "$accumulator"
    );

    public void validate(QueryPlan plan) {
        if (plan.collection() == null || plan.collection().isBlank()) {
            throw new IllegalArgumentException("collection is required");
        }
        if (!List.of("find", "aggregate").contains(plan.operation())) {
            throw new IllegalArgumentException("operation must be find|aggregate");
        }

        if ("find".equals(plan.operation())) {
            if (plan.criteria() != null) {
                for (var c : plan.criteria()) {
                    if (c.field() == null || c.field().isBlank())
                        throw new IllegalArgumentException("criteria field missing");
                    if (c.op() == null || !ALLOWED_CRITERIA_OPS.contains(c.op()))
                        throw new IllegalArgumentException("criteria op not allowed: " + c.op());
                }
            }
        }

        if ("aggregate".equals(plan.operation())) {
            if (plan.pipeline() == null || plan.pipeline().isEmpty()) {
                throw new IllegalArgumentException("aggregate pipeline required");
            }
            for (var st : plan.pipeline()) {
                if (st.stage() == null || st.stage().isBlank())
                    throw new IllegalArgumentException("pipeline stage missing");
                if (BLOCKED_MONGO_STAGES.contains(st.stage()))
                    throw new IllegalArgumentException("blocked pipeline stage: " + st.stage());
            }
        }

        // simple limit guard
        if (plan.limit() != null && plan.limit() > 5000) {
            throw new IllegalArgumentException("limit too high (max 5000)");
        }
    }
}

