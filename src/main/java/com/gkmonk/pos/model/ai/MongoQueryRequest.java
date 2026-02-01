package com.gkmonk.pos.model.ai;

import java.util.Map;

public record MongoQueryRequest(
        String collection,
        String intent,          // e.g. "find", "aggregate", "count"
        String requirement,     // plain english requirement
        Map<String, Object> schemaHint // optional: field types, sample doc keys
) {}
