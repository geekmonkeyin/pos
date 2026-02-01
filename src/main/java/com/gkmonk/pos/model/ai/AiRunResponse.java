package com.gkmonk.pos.model.ai;

import java.util.List;

public record AiRunResponse(List<QueryPlan> plans, String combinedAnalysis, Object rawResults) {}

