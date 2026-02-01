package com.gkmonk.pos.model.ai;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MongoPlanExecutor {
    private static final Logger log = LoggerFactory.getLogger(MongoPlanExecutor.class);
    private final MongoTemplate mongoTemplate;

    public MongoPlanExecutor(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Document> execute(QueryPlan plan) {
        try {
            if ("find".equals(plan.operation())) {
                Query q = new Query();
                if (plan.criteria() != null) {
                    for (var c : plan.criteria()) {
                        q.addCriteria(toCriteria(c));
                    }
                }
                if (plan.sort() != null && !plan.sort().isEmpty()) {
                    Sort sort = Sort.unsorted();
                    for (var s : plan.sort()) {
                        sort = sort.and("asc".equalsIgnoreCase(s.dir())
                                ? Sort.by(s.field()).ascending()
                                : Sort.by(s.field()).descending());
                    }
                    q.with(sort);
                }
                if (plan.skip() != null) q.skip(plan.skip());
                if (plan.limit() != null) q.limit(plan.limit());

                return mongoTemplate.find(q, Document.class, plan.collection());
            }

            // aggregate
            List<Document> pipelineDocs = new ArrayList<>();
            for (var st : plan.pipeline()) {
                pipelineDocs.add(new Document(st.stage(), st.value()));
            }
            MongoCollection<Document> collection = mongoTemplate.getCollection(plan.collection());
            return collection.aggregate(pipelineDocs).into(new ArrayList<>());
        } catch (Exception e) {
            log.error("[MONGO] Execution failed for collection '{}'. Plan: {}",
                    plan.collection(), plan, e);
            throw e;
        }
        }

    private Criteria toCriteria(QueryPlan.CriterionSpec c) {
        String f = c.field();
        Object v = c.value();
        return switch (c.op()) {
            case "eq" -> Criteria.where(f).is(v);
            case "ne" -> Criteria.where(f).ne(v);
            case "gt" -> Criteria.where(f).gt(v);
            case "gte" -> Criteria.where(f).gte(v);
            case "lt" -> Criteria.where(f).lt(v);
            case "lte" -> Criteria.where(f).lte(v);
            case "in" -> Criteria.where(f).in(asList(v));
            case "nin" -> Criteria.where(f).nin(asList(v));
            case "regex" -> Criteria.where(f).regex(String.valueOf(v), "i");
            case "exists" -> Criteria.where(f).exists(Boolean.TRUE.equals(v));
            default -> throw new IllegalArgumentException("Unsupported op: " + c.op());
        };
    }

    private List<?> asList(Object v) {
        if (v instanceof List<?> l) return l;
        return List.of(v);
    }
}

