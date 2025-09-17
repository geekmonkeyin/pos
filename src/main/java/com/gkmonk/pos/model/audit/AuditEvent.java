package com.gkmonk.pos.model.audit;

import com.mongodb.client.model.changestream.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonDocument;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document("audit_events")
public class AuditEvent {
    @Id private String id;
    private String orderId;
    private String db;
    private String collection;
    private OperationType operationType;          // insert | update | replace | delete | invalidate | drop | ...
    private Document documentKey;          // {"_id": ...}
    private Document fullDocument;         // “after” for insert/replace/update (with UPDATE_LOOKUP)
    private Document fullDocumentBeforeChange; // requires pre/post images enabled

    // Update details (for updates)
    private BsonDocument updatedFields;        // updateDescription.updatedFields
    private java.util.List<String> removedFields;

    // Timestamps / ordering
    private BsonTimestamp clusterTime;           // event.getClusterTime().getTime()
    private BsonDocument resumeToken;      // event.getResumeToken()

    // Your own metadata
    private String actor;                  // user/service (if you enrich it)
    private String source;                 // e.g., "orders-change-stream"

    // TTL support (optional)
    private Instant expiresAt;             // set if you want automatic expiry
}
