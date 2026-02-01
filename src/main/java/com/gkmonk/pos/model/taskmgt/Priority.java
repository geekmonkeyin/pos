package com.gkmonk.pos.model.taskmgt;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document("priorities")
public class Priority {
    @Id
    private String id;
    private String name;
    private String color;
    private int order;
}