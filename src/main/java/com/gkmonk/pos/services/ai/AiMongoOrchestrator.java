package com.gkmonk.pos.services.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkmonk.pos.model.ai.AiRunRequest;
import com.gkmonk.pos.model.ai.AiRunResponse;
import com.gkmonk.pos.model.ai.MongoPlanExecutor;
import com.gkmonk.pos.model.ai.QueryPlan;
import com.gkmonk.pos.model.ai.QueryPlanSafetyValidator;
import com.gkmonk.pos.registry.ai.CollectionRegistry;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiMongoOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiMongoOrchestrator.class);

    private final OllamaClient ollama;
    private final CollectionRegistry registry;
    private final ObjectMapper mapper;
    private final QueryPlanSafetyValidator validator;
    private final MongoPlanExecutor executor;

    public AiMongoOrchestrator(
            OllamaClient ollama,
            CollectionRegistry registry,
            ObjectMapper mapper,
            QueryPlanSafetyValidator validator,
            MongoPlanExecutor executor
    ) {
        this.ollama = ollama;
        this.registry = registry;
        this.mapper = mapper;
        this.validator = validator;
        this.executor = executor;
    }

    public Mono<AiRunResponse> run(AiRunRequest req) {
        int maxCols = req.maxCollections() == null ? 2 : Math.max(1, Math.min(req.maxCollections(), 5));

        return pickCollections(req.requirement(), maxCols)
                .flatMap(cols -> generatePlans(req.requirement(), cols))
                .flatMap(plans -> {
                    // validate + execute
                    Map<String, List<Document>> results = new LinkedHashMap<>();
                    for (QueryPlan p : plans) {
                        log.info("[AI] Executing plan for collection '{}': {}",
                                p.collection(), safeJson(p));
                        validator.validate(p);
                        results.put(p.collection(), executor.execute(p));
                    }
                    return analyze(req.requirement(), plans, results)
                            .map(analysis -> new AiRunResponse(plans, analysis, results));
                });
    }

    private Mono<List<String>> pickCollections(String requirement, int maxCols) {
        String registryJson;
        try {
            registryJson = mapper.writeValueAsString(
                    registry.list().stream().map(c -> Map.of(
                            "name", c.name(),
                            "description", c.description(),
                            "fields", c.fields()
                    )).toList()
            );
        } catch (Exception e) {
            registryJson = "[]";
        }

        String prompt = """
You are a MongoDB collection router.
Return ONLY valid JSON.

Pick up to %d collections that best match the requirement.
Output JSON EXACTLY:
{
  "collections": ["<name>", "<name>"],
  "reason": "<short>"
}

Requirement:
%s

Collections registry (JSON):
%s
""".formatted(maxCols, requirement, registryJson);

        return ollama.generate(prompt)
                .map(this::extractJson)
                .map(json -> {
                    try {
                        var node = mapper.readTree(json);
                        List<String> cols = new ArrayList<>();
                        node.path("collections").forEach(x -> cols.add(x.asText()));
                        if (cols.isEmpty()) throw new RuntimeException("AI picked no collections");
                        return cols;
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid router JSON:\n" + json, e);
                    }
                });
    }

    private Mono<List<QueryPlan>> generatePlans(String requirement, List<String> collections) {
        // Build prompt containing chosen collection schemas
        List<Map<String, Object>> metas = new ArrayList<>();
        for (String c : collections) {
            var meta = registry.get(c).orElse(null);
            if (meta != null) {
                metas.add(Map.of(
                        "name", meta.name(),
                        "description", meta.description(),
                        "fields", meta.fields()
                ));
            }
        }

        String metaJson;
        try { metaJson = mapper.writeValueAsString(metas); }
        catch (Exception e) { metaJson = "[]"; }

        String prompt = """
You are an expert MongoDB engineer using Spring MongoTemplate.
Return ONLY valid JSON.

Create one query plan per collection (find or aggregate).
Prefer "find" unless aggregation is clearly needed.

Allowed criteria ops: eq, ne, gt, gte, lt, lte, in, nin, regex, exists
Do NOT use $where, $function, $accumulator.

Output JSON EXACTLY:
{
  "plans": [
    {
      "collection": "name",
      "operation": "find|aggregate",
      "criteria": [{"field":"x","op":"eq","value":"y"}],
      "sort": [{"field":"createdAt","dir":"desc"}],
      "limit": 100,
      "skip": 0,
      "pipeline": [{"stage":"$match","value":{...}}],
      "explanation": "..."
    }
  ]
}

Require ment:
%s

Chosen collections (with schema):
%s
""".formatted(requirement, metaJson);

        return ollama.generate(prompt)
                .map(this::extractJson)
                .map(json -> {
                    try {
                        var node = mapper.readTree(json).path("plans");
                        List<QueryPlan> plans = new ArrayList<>();
                        for (var p : node) {
                            plans.add(mapper.convertValue(p, QueryPlan.class));
                        }
                        if (plans.isEmpty()) throw new RuntimeException("No plans generated");
                        return plans;
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid plans JSON:\n" + json, e);
                    }
                });
    }

    private Mono<String> analyze(String requirement, List<QueryPlan> plans, Map<String, List<Document>> results) {
        String plansJson, resultsJson;
        try { plansJson = mapper.writeValueAsString(plans); } catch (Exception e) { plansJson = String.valueOf(plans); }
        try { resultsJson = mapper.writeValueAsString(results); } catch (Exception e) { resultsJson = String.valueOf(results); }

        String prompt = """
You are a data analyst for Geekmonkey operations.
Return ONLY valid JSON.

Given:
- requirement
- query plans used
- database results

Provide:
- summary: what the data says
- insights: key metrics, counts, noteworthy items
- warnings: empty results, missing fields, performance issues, next steps

Output JSON EXACTLY:
{
  "summary": "...",
  "insights": "...",
  "warnings": "..."
}

Requirement:
%s

Plans used:
%s

DB results:
%s
""".formatted(requirement, plansJson, resultsJson);

        return ollama.generate(prompt)
                .doOnNext(raw -> log.info("[LLM][ANALYSIS] Raw analysis response:\n{}", shorten(raw)))
                .map(this::extractJson)
                .doOnNext(json -> log.debug("[LLM][ANALYSIS] Parsed JSON:\n{}", json))
                .map(json -> {
                    try {
                        var n = mapper.readTree(json);
                        return "SUMMARY:\n" + n.path("summary").asText("")
                                + "\n\nINSIGHTS:\n" + n.path("insights").asText("")
                                + "\n\nWARNINGS:\n" + n.path("warnings").asText("");
                    } catch (Exception e) {
                        // If model returns non-json (rare), fallback to raw
                        return json;
                    }
                });
    }

    private String extractJson(String text) {
        int s = text.indexOf('{');
        int e = text.lastIndexOf('}');
        if (s >= 0 && e > s) return text.substring(s, e + 1).trim();
        log.error("[LLM] JSON extraction failed. Raw text:\n{}", text);
        throw new RuntimeException("No JSON found in AI output:\n" + text);
    }

    private String safeJson(Object o) {
        try { return mapper.writeValueAsString(o); }
        catch (Exception e) { return String.valueOf(o); }
    }

    private String shorten(String s) {
        if (s == null) return null;
        int max = 4000;
        return s.length() <= max ? s : s.substring(0, max) + "…[truncated]";
    }
}
