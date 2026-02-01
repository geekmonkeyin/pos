package com.gkmonk.pos.model.ai;

public record MongoAnalyzeRequest(
        String requirement,
        String queryUsed,     // the query JSON as string (or your pipeline)
        String dbResponseJson // response from Mongo as JSON string
) {}
