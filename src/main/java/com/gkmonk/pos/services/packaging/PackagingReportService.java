package com.gkmonk.pos.services.packaging;

import com.gkmonk.pos.model.packaging.PackagingFilter;
import com.gkmonk.pos.model.packaging.PackagingRow;
import com.gkmonk.pos.model.packaging.PackagingSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@AllArgsConstructor
public class PackagingReportService {

    private final MongoTemplate mongo;

    // Your carton catalog; sort from smallest to largest
    private final List<Box> boxCatalog = List.of(
            new Box("XS", 10, 10, 5),
            new Box("S",  20, 15, 10),
            new Box("M",  30, 20, 15),
            new Box("L",  40, 30, 20),
            new Box("XL", 50, 35, 25)
    );

    @Data @AllArgsConstructor
    static class Box {
        String code;
        double L; double W; double H; // cm
        double volume() { return L * W * H; }
    }

    /* --------------------------- PUBLIC APIS --------------------------- */

    public PageImpl<PackagingRow> findRows(PackagingFilter f) {
        List<AggregationOperation> stages = new ArrayList<>();

        // 1) MATCH (date + filters)
        Criteria filters = buildCriteria(f);
        if (filters != null) stages.add(match(filters));

        // 2) COMPUTE WEIGHTS
        int divisor = Optional.ofNullable(f.getDivisor()).orElse(5000);
        Document vol = new Document("$divide",
                List.of(new Document("$multiply", List.of("$length", "$width", "$height")), divisor));

        stages.add(addFields()
                .addField("volumetricWeightKg").withValue(vol)
                .addField("chargeableWeightKg").withValue(
                        new Document("$cond", List.of(
                                new Document("$gt", List.of("$actualWeightKg", null)),
                                new Document("$max", List.of("$actualWeightKg", vol)),
                                vol
                        ))
                ).build());

        // 3) EXTRA WEIGHT FILTERS (on computed field)
        Criteria cwCrit = buildChargeableRange(f);
        if (cwCrit != null) stages.add(match(cwCrit));

        // 4) SORT / PAGE
        int page = Math.max(0, Optional.ofNullable(f.getPage()).orElse(0));
        int size = Math.max(1, Optional.ofNullable(f.getSize()).orElse(50));
        stages.add(sort(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "chargeableWeightKg")));
        stages.add(skip((long) page * size));
        stages.add(limit(size));

        // 5) PROJECT
        stages.add(project("orderNo","length","width","height","actualWeightKg",
                "volumetricWeightKg","chargeableWeightKg",
                "courier","warehouse","status"));

        Aggregation agg = newAggregation(stages);
        AggregationResults<Document> results = mongo.aggregate(agg, "orders", Document.class);

        List<PackagingRow> rows = results.getMappedResults().stream()
                .map(d -> PackagingRow.builder()
                        .id(d.getObjectId("_id") != null ? d.getObjectId("_id").toHexString() : null)
                        .orderNo(d.getString("orderNo"))
                        .length(getDouble(d, "length"))
                        .width(getDouble(d, "width"))
                        .height(getDouble(d, "height"))
                        .actualWeightKg(getDouble(d, "actualWeightKg"))
                        .volumetricWeightKg(round2(getDouble(d, "volumetricWeightKg")))
                        .chargeableWeightKg(round2(getDouble(d, "chargeableWeightKg")))
                        .courier(d.getString("courier"))
                        .warehouse(d.getString("warehouse"))
                        .status(d.getString("status"))
                        .recommendedBoxCode(recommendBox(d))
                        .build())
                .collect(Collectors.toList());

        long total = countTotal(f);
        return new PageImpl<>(rows, Pageable.ofSize(size).withPage(page), total);
    }

    public PackagingSummary buildSummary(PackagingFilter f) {
        List<AggregationOperation> stages = new ArrayList<>();

        // 1) MATCH (date + filters)
        Criteria filters = buildCriteria(f);
        if (filters != null) stages.add(match(filters));

        // 2) COMPUTE WEIGHTS
        int divisor = Optional.ofNullable(f.getDivisor()).orElse(5000);
        Document vol = new Document("$divide",
                List.of(new Document("$multiply", List.of("$length", "$width", "$height")), divisor));

        stages.add(addFields()
                .addField("volumetricWeightKg").withValue(vol)
                .addField("chargeableWeightKg").withValue(
                        new Document("$cond", List.of(
                                new Document("$gt", List.of("$actualWeightKg", null)),
                                new Document("$max", List.of("$actualWeightKg", vol)),
                                vol
                        ))
                ).build());

        // 3) RANGE ON CHARGEABLE
        Criteria cwCrit = buildChargeableRange(f);
        if (cwCrit != null) stages.add(match(cwCrit));

        // 4) SUMMARY
        stages.add(group()
                .count().as("total")
                .avg("volumetricWeightKg").as("avgVol")
                .avg("actualWeightKg").as("avgAct")
                .avg("chargeableWeightKg").as("avgCw"));

        Aggregation agg = newAggregation(stages);
        Document summaryDoc = mongo.aggregate(agg, "orders", Document.class).getUniqueMappedResult();

        long total = summaryDoc != null ? summaryDoc.getLong("total") : 0L;
        double avgVol = summaryDoc != null ? getDouble(summaryDoc, "avgVol") : 0.0;
        double avgAct = summaryDoc != null ? getDouble(summaryDoc, "avgAct") : 0.0;
        double avgCw  = summaryDoc != null ? getDouble(summaryDoc, "avgCw")  : 0.0;

        Map<String, Long> bands = fetchBands(f);

        return PackagingSummary.builder()
                .totalCount(total)
                .avgVolumetricKg(round2(avgVol))
                .avgActualKg(round2(avgAct))
                .avgChargeableKg(round2(avgCw))
                .weightBands(bands)
                .build();
    }

    /* --------------------------- INTERNALS --------------------------- */

    private Criteria buildCriteria(PackagingFilter f) {
        List<Criteria> ands = new ArrayList<>();

        // Date range on BSON Date field: orderDate
        if (f.getFromDate() != null || f.getToDate() != null) {
            Criteria c = Criteria.where("orderDate");
            if (f.getFromDate() != null) {
                Date from = Date.from(f.getFromDate().atStartOfDay().toInstant(ZoneOffset.UTC));
                c = c.gte(from);
            }
            if (f.getToDate() != null) {
                Date to = Date.from(f.getToDate().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)); // exclusive
                c = c.lt(to);
            }
            ands.add(c);
        }

        // Text search across orderNo / customerName / pincode
        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            String regex = ".*" + Pattern.quote(f.getSearch()) + ".*";
            Criteria or = new Criteria().orOperator(
                    Criteria.where("orderNo").regex(regex, "i"),
                    Criteria.where("customerName").regex(regex, "i"),
                    Criteria.where("pincode").regex(regex, "i")
            );
            ands.add(or);
        }

        if (isNotBlank(f.getCourier()))   ands.add(Criteria.where("courier").is(f.getCourier()));
        if (isNotBlank(f.getWarehouse())) ands.add(Criteria.where("warehouse").is(f.getWarehouse()));
        if (isNotBlank(f.getStatus()))    ands.add(Criteria.where("status").is(f.getStatus()));

        if (Boolean.TRUE.equals(f.getHasActual())) {
            ands.add(Criteria.where("actualWeightKg").ne(null));
        }
        if (Boolean.TRUE.equals(f.getMissingDims())) {
            ands.add(new Criteria().orOperator(
                    Criteria.where("length").is(null),
                    Criteria.where("width").is(null),
                    Criteria.where("height").is(null)
            ));
        }

        if (ands.isEmpty()) return null;
        if (ands.size() == 1) return ands.get(0);
        return new Criteria().andOperator(ands.toArray(new Criteria[0]));
    }

    private Criteria buildChargeableRange(PackagingFilter f) {
        if (f.getMinCw() == null && f.getMaxCw() == null) return null;
        Criteria c = Criteria.where("chargeableWeightKg");
        if (f.getMinCw() != null) c = c.gte(f.getMinCw());
        if (f.getMaxCw() != null) c = c.lte(f.getMaxCw());
        return c;
    }

    private Map<String, Long> fetchBands(PackagingFilter f) {
        List<AggregationOperation> stages = new ArrayList<>();

        // Reuse filters
        Criteria filters = buildCriteria(f);
        if (filters != null) stages.add(match(filters));

        int divisor = Optional.ofNullable(f.getDivisor()).orElse(5000);
        Document vol = new Document("$divide",
                List.of(new Document("$multiply", List.of("$length", "$width", "$height")), divisor));

        stages.add(addFields()
                .addField("cw").withValue(
                        new Document("$cond", List.of(
                                new Document("$gt", List.of("$actualWeightKg", null)),
                                new Document("$max", List.of("$actualWeightKg", vol)),
                                vol
                        ))
                ).build());

        // Bucket slabs
        stages.add(new AggregationOperation() {
            @Override
            public Document toDocument(org.springframework.data.mongodb.core.aggregation.AggregationOperationContext context) {
                return new Document("$bucket",
                        new Document("groupBy", "$cw")
                                .append("boundaries", List.of(0,0.5,1,2,3,5,10,20,9999))
                                .append("default", "20kg+")
                                .append("output", new Document("count", new Document("$sum", 1))));
            }
        });

        Aggregation agg = newAggregation(stages);
        List<Document> buckets = mongo.aggregate(agg, "orders", Document.class).getMappedResults();

        // Ordered labels map
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        List<String> labels = List.of("0–0.5","0.5–1","1–2","2–3","3–5","5–10","10–20","20kg+");
        labels.forEach(l -> map.put(l, 0L));

        for (Document b : buckets) {
            Object id = b.get("_id");
            long count = b.get("count", Number.class).longValue();
            String label;
            if (id instanceof Number) {
                double min = ((Number) id).doubleValue();
                // map boundary to label:
                if      (min == 0.0)   label = "0–0.5";
                else if (min == 0.5)   label = "0.5–1";
                else if (min == 1.0)   label = "1–2";
                else if (min == 2.0)   label = "2–3";
                else if (min == 3.0)   label = "3–5";
                else if (min == 5.0)   label = "5–10";
                else if (min == 10.0)  label = "10–20";
                else                   label = "20kg+";
            } else {
                label = "20kg+";
            }
            map.put(label, map.getOrDefault(label, 0L) + count);
        }
        return map;
    }

    private long countTotal(PackagingFilter f) {
        List<AggregationOperation> stages = new ArrayList<>();
        Criteria filters = buildCriteria(f);
        if (filters != null) stages.add(match(filters));
        stages.add(group().count().as("c"));
        Aggregation agg = newAggregation(stages);
        Document d = mongo.aggregate(agg, "orders", Document.class).getUniqueMappedResult();
        return d == null ? 0L : d.get("c", Number.class).longValue();
    }

    private static double round2(Double d) {
        if (d == null) return 0.0;
        return Math.round(d * 100.0) / 100.0;
    }

    private static Double getDouble(Document d, String key) {
        Object v = d.get(key);
        return v == null ? null : ((Number) v).doubleValue();
    }

    // Recommend smallest box that fits (done in Java for clarity)
    private String recommendBox(Document d) {
        Double L = getDouble(d, "length");
        Double W = getDouble(d, "width");
        Double H = getDouble(d, "height");
        if (L == null || W == null || H == null) return "-";

        return boxCatalog.stream()
                .filter(b -> b.L >= L && b.W >= W && b.H >= H)
                .sorted(Comparator.comparingDouble(Box::volume))
                .map(Box::getCode)
                .findFirst()
                .orElse("CUSTOM");
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
