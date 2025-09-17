package com.gkmonk.pos.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("counters")
@Data
public class Counters {
    @Id
    private String id;
    private long seq;

}
