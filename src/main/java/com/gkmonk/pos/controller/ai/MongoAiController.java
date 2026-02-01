package com.gkmonk.pos.controller.ai;


import com.gkmonk.pos.model.ai.AiRunRequest;
import com.gkmonk.pos.model.ai.AiRunResponse;
import com.gkmonk.pos.model.ai.MongoAnalyzeRequest;
import com.gkmonk.pos.model.ai.MongoAnalyzeResponse;
import com.gkmonk.pos.model.ai.MongoQueryRequest;
import com.gkmonk.pos.model.ai.MongoQueryResult;
import com.gkmonk.pos.services.ai.AiMongoOrchestrator;
import com.gkmonk.pos.services.ai.MongoAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/ai/mongo")
public class MongoAiController {

    @Autowired
    private MongoAiService service;
    @Autowired
    private AiMongoOrchestrator orchestrator;

    @PostMapping("/query-and-analyze")
    public Mono<AiRunResponse> run(@RequestBody AiRunRequest req) {
        return orchestrator.run(req);
    }

    @PostMapping("/query")
    public Mono<MongoQueryResult> generateQuery(@RequestBody MongoQueryRequest req) {
        return service.generateMongoQuery(req);
    }

    @PostMapping("/analyze")
    public Mono<MongoAnalyzeResponse> analyze(@RequestBody MongoAnalyzeRequest req) {
        return service.analyzeMongoResponse(req);
    }
}
