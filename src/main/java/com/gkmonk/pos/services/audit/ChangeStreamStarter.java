package com.gkmonk.pos.services.audit;

import com.gkmonk.pos.model.audit.AuditEvent;
import com.gkmonk.pos.model.audit.ChangeStreamOffset;
import com.gkmonk.pos.model.logs.TaskLogs;
import com.gkmonk.pos.model.logs.TaskStatusType;
import com.gkmonk.pos.model.logs.TaskType;
import com.gkmonk.pos.repo.audit.AuditEventRepo;
import com.gkmonk.pos.repo.audit.ChangeStreamOffsetRepo;
import com.gkmonk.pos.services.logs.TaskLogsServiceImpl;
import com.gkmonk.pos.utils.TaskPoints;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.FullDocumentBeforeChange;
import com.mongodb.client.model.changestream.UpdateDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeStreamStarter {

    @Autowired
    private TaskLogsServiceImpl taskLogsService;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    private AuditEventRepo auditEventRepository;
    @Autowired
    private ChangeStreamOffsetRepo changeStreamOffsetRepo;
    @Bean
    ApplicationRunner runner() {
        return args -> {
            // $match pipeline: only insert/update/replace
            Document match = new Document("$match",
                    new Document("operationType", new Document("$in", List.of("insert","update","replace"))));

            ChangeStreamOptions options = ChangeStreamOptions.builder()
                    .filter(match)
                  //  .resumeToken(getLastResumeToken())
                    .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
                    .fullDocumentBeforeChangeLookup(FullDocumentBeforeChange.WHEN_AVAILABLE)
                   // .maxAwaitTime(Duration.ofSeconds(5))
                    .build();

            Flux<org.springframework.data.mongodb.core.ChangeStreamEvent<Document>> stream =
                    reactiveMongoTemplate.changeStream("inventory_shopify", options, Document.class);


            stream
                    .doOnSubscribe(s -> log.info("Subscribed to orders change stream"))
                    .doOnNext(evt -> {
                        log.info("Change: type={} key={} body={}",
                                evt.getOperationType(),
                                evt.getBody());
                         mapToAuditEvent(evt);

                        }
                    )
                    .doOnError(err -> log.error("Change stream error", err))
                    // keep the subscription alive
                    .retryWhen(reactor.util.retry.Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2))
                            .maxBackoff(Duration.ofMinutes(1)))
                    .subscribe();
        };
    }

    private BsonDocument getLastResumeToken() {
        return changeStreamOffsetRepo.findById("inventory_shopify")
                .map(ChangeStreamOffset::getResumeToken)
                .orElse(null);
    }

    private void mapToAuditEvent(ChangeStreamEvent<Document> evt) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setDb(evt.getDatabaseName());
        auditEvent.setCollection(evt.getCollectionName());
        auditEvent.setOperationType(evt.getOperationType());
        auditEvent.setRemovedFields(getRemovedFields(Objects.requireNonNull(evt.getRaw()).getUpdateDescription()));
        auditEvent.setUpdatedFields(getUpdatedFields(Objects.requireNonNull(evt.getRaw()).getUpdateDescription()));
        auditEvent.setFullDocument(evt.getRaw().getFullDocument());
        auditEvent.setClusterTime(evt.getRaw().getClusterTime());
        auditEvent.setFullDocumentBeforeChange(evt.getRaw().getFullDocumentBeforeChange());
        auditEvent.setResumeToken(evt.getRaw().getResumeToken());
        updateResumeToken(evt.getResumeToken());
        log.info("Audit Event: {}", auditEvent);
        updateTaskLog(auditEvent,"inventory_update");
        auditEventRepository.save(auditEvent);
    }

    private void updateTaskLog(AuditEvent auditEvent, String inventoryUpdate) {
        TaskLogs taskLogs = new TaskLogs();
        if(auditEvent.getFullDocument() != null) {
            Document full = auditEvent.getFullDocument();
            if (full.get("stockHistory") != null) {
                List<Document> stockHistory = (List) full.get("stockHistory");
                taskLogs.setDeviceId(getDeviceId(stockHistory));
                taskLogs.setPoints(calculatePoints(stockHistory,TaskPoints.PRODUCT_POINT.points));
                taskLogs.setTaskName(TaskType.UPDATE_INVENTORY.name());
                String remark = stockHistory.get(stockHistory.size()-1).getString("remarks");
                String productName = stockHistory.get(stockHistory.size()-1).getString("productName");
                taskLogs.setMetaData(remark + " for " + productName + ", qty:"+ stockHistory.get(stockHistory.size()-1).getInteger("quantity"));
                taskLogs.setStatus(taskLogs.getPoints() > 0 ? TaskStatusType.COMPLETED.name() : TaskStatusType.FAILED.name());
                Date latestUpdate = (Date) stockHistory.get(stockHistory.size() - 1).get("updatedDate");
                taskLogs.setTaskDate(latestUpdate.toString());
                taskLogs.setEmpName(stockHistory.get(stockHistory.size() - 1).get("empName") != null ? stockHistory.get(stockHistory.size() - 1).getString("empName") : "System");
                taskLogs.setEmpId(stockHistory.get(stockHistory.size() - 1).get("empId") != null ? stockHistory.get(stockHistory.size() - 1).getString("empId") : "System");
                taskLogsService.saveTaskLog(taskLogs);

            }
        }


    }

    private int calculatePoints(List<Document> stockHistory, int points) {
        int quantity = (int) stockHistory.get(stockHistory.size() - 1).get("quantity");
        if(stockHistory.size() == 1){
            return quantity * points;
        }

        Date latestUpdate = (Date) stockHistory.get(stockHistory.size() - 1).get("updatedDate");
        Date lastUpdate = (Date) stockHistory.get(stockHistory.size() - 2).get("updatedDate");

        //compare 2 dates in days
        long diff = latestUpdate.getTime() - lastUpdate.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        if(days == 0){
            return 0;
        } else {
            return quantity * points ;
        }

    }

    private String getDeviceId(List<Document> stockHistory) {
        if(stockHistory != null && !stockHistory.isEmpty()){
                    Document last = stockHistory.get(stockHistory.size() - 1);
                    if(last.get("deviceName") != null){
                        return last.getString("deviceName");
                    }
                }
        return "Desktop";
    }

    private void updateResumeToken(BsonValue resumeToken) {
        // Save the resume token to a persistent storage (e.g., database, file, etc.)
        // For simplicity, we are just logging it here
        log.info("Updated resume token: {}", resumeToken);
        ChangeStreamOffset changeStreamOffset =  new ChangeStreamOffset();
        changeStreamOffset.setId("outbound_orders");
        changeStreamOffset.setResumeToken(resumeToken.asDocument());
        changeStreamOffsetRepo.save(changeStreamOffset);
    }

    private BsonDocument getUpdatedFields(UpdateDescription updateDescription) {
       return updateDescription != null ? updateDescription.getUpdatedFields() : new BsonDocument();
   }

    private List<String> getRemovedFields(UpdateDescription updateDescription) {
        return updateDescription != null ? updateDescription.getRemovedFields() : List.of();
    }
}
