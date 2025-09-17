package com.gkmonk.pos.model.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("change_stream_offsets")
public class ChangeStreamOffset {
    @Id
    private String id;                 // e.g., "orders"
    private BsonDocument resumeToken;
}