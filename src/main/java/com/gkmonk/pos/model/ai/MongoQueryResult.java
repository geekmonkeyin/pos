package com.gkmonk.pos.model.ai;

import java.util.Map;

public record MongoQueryResult(
        String collection,
        String intent,
        Map<String, Object> query,        // find filter or aggregate pipeline wrapper
        Map<String, Object> projection,   // optional
        Map<String, Object> sort,         // optional
        Integer limit,                    // optional
        String explanation                // short explanation
) {}
