package com.gkmonk.pos.services.ai;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkmonk.pos.model.ai.MongoAnalyzeRequest;
import com.gkmonk.pos.model.ai.MongoAnalyzeResponse;
import com.gkmonk.pos.model.ai.MongoQueryRequest;
import com.gkmonk.pos.model.ai.MongoQueryResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class MongoAiService {

    private final OllamaClient ollama;
    private final ObjectMapper mapper;

    public MongoAiService(OllamaClient ollama, ObjectMapper mapper) {
        this.ollama = ollama;
        this.mapper = mapper;
    }

    public Mono<MongoQueryResult> generateMongoQuery(MongoQueryRequest req) {
        String prompt = buildQueryPrompt(req);

        return ollama.generate(prompt)
                .map(this::extractJsonBlock)
                .map(json -> {
                    try {
                        // We expect JSON with keys:
                        // collection, intent, query, projection, sort, limit, explanation
                        Map<String, Object> map = mapper.readValue(json, new TypeReference<>() {});
                        return new MongoQueryResult(
                                (String) map.getOrDefault("collection", req.collection()),
                                (String) map.getOrDefault("intent", req.intent()),
                                castMap(map.get("query")),
                                castMap(map.get("projection")),
                                castMap(map.get("sort")),
                                map.get("limit") == null ? null : ((Number) map.get("limit")).intValue(),
                                (String) map.getOrDefault("explanation", "")
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("AI returned invalid JSON. Output:\n" + json, e);
                    }
                });
    }

    public Mono<MongoAnalyzeResponse> analyzeMongoResponse(MongoAnalyzeRequest req) {
        String prompt = buildAnalyzePrompt(req);
        return ollama.generate(prompt)
                .map(this::extractJsonBlock)
                .map(json -> {
                    try {
                        Map<String, Object> map = mapper.readValue(json, new TypeReference<>() {});
                        return new MongoAnalyzeResponse(
                                (String) map.getOrDefault("summary", ""),
                                (String) map.getOrDefault("insights", ""),
                                (String) map.getOrDefault("issuesOrWarnings", "")
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("AI returned invalid JSON. Output:\n" + json, e);
                    }
                });
    }

    private String buildQueryPrompt(MongoQueryRequest req) {
        String schema = (req.schemaHint() == null || req.schemaHint().isEmpty())
                ? ""
                : "\nSchema hint (JSON): " + safeJson(req.schemaHint());

        return """
You are an expert MongoDB engineer.
Return ONLY valid JSON. No markdown, no explanation outside JSON.

Task:
- Generate a MongoDB query (find filter or aggregate pipeline) for the requirement.
- Keep it correct and minimal.
- If unsure, make reasonable assumptions.

Output JSON format EXACTLY:
{
  "collection": "<collection>",
  "intent": "find|aggregate|count",
  "query": { ... },
  "projection": { ... } or null,
  "sort": { ... } or null,
  "limit": <number> or null,
  "explanation": "<1-2 lines>"
}

Inputs:
Collection: %s
Intent: %s
Requirement: %s
%s
""".formatted(
                nullToEmpty(req.collection()),
                nullToEmpty(req.intent()),
                nullToEmpty(req.requirement()),
                schema
        );
    }

    private String buildAnalyzePrompt(MongoAnalyzeRequest req) {
        return """
You are an expert data analyst.
Return ONLY valid JSON. No markdown.

You will be given:
- requirement
- mongo query used
- db response (JSON)

Your job:
1) Summarize what the response means in plain English.
2) Provide key insights (counts, anomalies, trends).
3) Warn about issues: empty results, missing fields, likely query bug, performance risks.

Output JSON format EXACTLY:
{
  "summary": "...",
  "insights": "...",
  "issuesOrWarnings": "..."
}

Requirement:
%s

Mongo query used:
%s

DB response JSON:
%s
""".formatted(
                nullToEmpty(req.requirement()),
                nullToEmpty(req.queryUsed()),
                nullToEmpty(req.dbResponseJson())
        );
    }

    // Extracts JSON even if model adds extra text (common issue)
    private String extractJsonBlock(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1).trim();
        throw new RuntimeException("No JSON object found in AI output:\n" + text);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object o) {
        if (o == null) return null;
        if (o instanceof Map<?, ?> m) return (Map<String, Object>) m;
        throw new IllegalArgumentException("Expected a JSON object but got: " + o);
    }

    private String safeJson(Object o) {
        try { return mapper.writeValueAsString(o); }
        catch (Exception e) { return String.valueOf(o); }
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }
}
