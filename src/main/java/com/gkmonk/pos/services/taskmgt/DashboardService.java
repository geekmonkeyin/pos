package com.gkmonk.pos.services.taskmgt;


import com.gkmonk.pos.model.taskmgt.Consts;
import com.gkmonk.pos.model.taskmgt.MiscDtos;
import com.gkmonk.pos.repo.taskmgt.UserRepo;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MongoTemplate mongoTemplate;
    private final UserRepo userRepo;

    public List<MiscDtos.PerformanceMetric> performance() {
        // Equivalent to python aggregation :contentReference[oaicite:4]{index=4}
        Aggregation agg = newAggregation(
                match(Criteria.where("status").is(Consts.TaskStatus.COMPLETED)),
                group("assignedTo")
                        .count().as("tasksCompleted")
                        .sum("timeTaken").as("totalTime")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "taskmgt_tasks", Document.class);

        List<MiscDtos.PerformanceMetric> out = new ArrayList<>();
        for (Document d : results.getMappedResults()) {
            Object idObj = d.get("_id");
            if (idObj == null) continue;
            String userId = String.valueOf(idObj);

            var userOpt = userRepo.findById(userId);
            if (userOpt.isEmpty()) continue;

            var u = userOpt.get();
            out.add(MiscDtos.PerformanceMetric.builder()
                    .userId(userId)
                    .username(u.getUsername())
                    .full_name(u.getFull_name())
                    .tasksCompleted(((Number) d.get("tasksCompleted")).intValue())
                    .totalTime(((Number) d.get("totalTime")).intValue())
                    .build());
        }

        return out.stream()
                .sorted(Comparator.comparingInt(MiscDtos.PerformanceMetric::getTasksCompleted).reversed())
                .collect(Collectors.toList());
    }
}
