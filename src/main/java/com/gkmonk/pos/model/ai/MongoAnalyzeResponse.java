package com.gkmonk.pos.model.ai;

public record MongoAnalyzeResponse(
        String summary,
        String insights,
        String issuesOrWarnings
) {}
