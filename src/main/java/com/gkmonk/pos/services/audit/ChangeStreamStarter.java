package com.gkmonk.pos.services.audit;

import com.gkmonk.pos.model.audit.AuditEvent;
import com.gkmonk.pos.model.audit.ChangeStreamOffset;
import com.gkmonk.pos.repo.audit.AuditEventRepo;
import com.gkmonk.pos.repo.audit.ChangeStreamOffsetRepo;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeStreamStarter {

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
                    reactiveMongoTemplate.changeStream("outbound_orders", options, Document.class);


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
        return changeStreamOffsetRepo.findById("outbound_orders")
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
        auditEventRepository.save(auditEvent);
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
