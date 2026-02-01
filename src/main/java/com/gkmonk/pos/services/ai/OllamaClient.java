package com.gkmonk.pos.services.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class OllamaClient {

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String model;
    private final Duration timeout;
    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);


    public OllamaClient(
            ObjectMapper mapper,
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String model,
            @Value("${ollama.timeout-seconds:180000}") long timeoutSeconds
    ) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.mapper = mapper;
        this.model = model;
           this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

//    public Mono<String> generate(String prompt) {
//
//        String requestId = UUID.randomUUID().toString();
//
//        log.info("[LLM][{}] Prompt sent to Ollama:\n{}", requestId, shorten(prompt));
//
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("model", model);
//        body.put("prompt", prompt);
//        body.put("stream", false);
//
//        Map<String, Object> options = new HashMap<>();
//        options.put("temperature", 0.2);
//        options.put("top_p", 0.9);
//        body.put("options", options);
//
//        return webClient.post()
//                .uri("/api/generate")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(String.class)
//                .timeout(timeout)
//                .doOnNext(raw -> {
//            log.info("[LLM][{}] Raw response:\n{}", requestId, shorten(raw));
//        })
//                .map(raw -> {
//                    try {
//                        JsonNode root = mapper.readTree(raw);
//                        return root.path("response").asText("");
//                    } catch (Exception e) {
//                        throw new RuntimeException("Failed to parse Ollama response: " + raw, e);
//                    }
//                })
//                .doOnError(ex -> {
//                    log.error("[LLM][{}] Ollama call failed", requestId, ex);
//                });
//    }

    public Mono<String> generate(String prompt) {

        return Mono.defer(() -> {

            String requestId = UUID.randomUUID().toString();
            log.info("[LLM][{}] Prompt sent to Ollama:\n{}", requestId, shorten(prompt));

            try {
                // ---------- Build request body ----------
                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("prompt", prompt);
                body.put("stream", false);

                Map<String, Object> options = new HashMap<>();
                options.put("temperature", 0.2);
                options.put("top_p", 0.9);

                // Speed & stability knobs (recommended)
                options.put("num_ctx", 1024);
                options.put("num_predict", 150);

                body.put("options", options);

                String json = mapper.writeValueAsString(body);

                // ---------- HTTP client ----------
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434" + "/api/generate"))
                        .timeout(timeout != null ? timeout : Duration.ofMinutes(3))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                // ---------- SYNC CALL ----------
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                log.info("[LLM][{}] HTTP Status: {}", requestId, response.statusCode());
                log.info("[LLM][{}] Raw response:\n{}", requestId, shorten(response.body()));

                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    throw new RuntimeException(
                            "Ollama HTTP " + response.statusCode() + ": " + response.body());
                }

                JsonNode root = mapper.readTree(response.body());
                return Mono.just(root.path("response").asText(""));

            } catch (Exception e) {
                log.error("[LLM][{}] Ollama call failed", requestId, e);
                return Mono.error(new RuntimeException("Ollama call failed", e));
            }
        });
    }


    private String shorten(String s) {
        if (s == null) return null;
        int max = 4000;
        return s.length() <= max ? s : s.substring(0, max) + "…[truncated]";
    }

        public static void main(String[] args) {
            try {
                String ollamaUrl = "http://192.168.31.219:11434/api/generate";
                // use http://localhost:11434 if same machine

                String requestBody = """
                {
                  "model": "qwen2.5:7b",
                  "prompt": "Return ONLY JSON. Generate MongoDB query to fetch last 5 days attendance from attendance_sessions.",
                  "temperature": 0,
                  "num_ctx": 1024,
                  "num_predict": 180,
                  "keep_alive": "30m",
                  "stream": false
                }
                """;

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ollamaUrl))
                        .timeout(Duration.ofMinutes(3))   // IMPORTANT for slow models
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("HTTP Status: " + response.statusCode());
                System.out.println("Response:");
                System.out.println(response.body());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
