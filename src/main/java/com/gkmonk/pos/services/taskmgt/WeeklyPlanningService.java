package com.gkmonk.pos.services.taskmgt;

import com.gkmonk.pos.model.taskmgt.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeeklyPlanningService {

    private final MongoTemplate mongoTemplate;

    public Map<String, List<Task>> weekly() {
        // Same approach as python: monday -> saturday, query due_date regex prefix :contentReference[oaicite:5]{index=5}
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        Map<String, List<Task>> out = new LinkedHashMap<>();

        for (int i = 0; i < days.length; i++) {
            LocalDate d = monday.plusDays(i);
            String prefix = d.toString(); // YYYY-MM-DD
            Query q = new Query(Criteria.where("dueDate").regex("^" + prefix));
            List<Task> tasks = mongoTemplate.find(q, Task.class, "taskmgt_tasks");
            out.put(days[i], tasks);
        }

        return out;
    }
}