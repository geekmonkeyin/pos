package com.gkmonk.pos.model.taskmgt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document("taskmgt_tasks")
public class Task {
    @Id
    private String id;

    private String title;
    private String description;
    private String priorityId;
    private String teamTypeId;
    private String status;       // todo/in_progress/completed/blocked
    private String assignedTo;   // user id
    private String dueDate;      // ISO string prefix match

    private String createdBy;    // user id
    private String createdAt;    // ISO string
    private String updatedAt;    // ISO string

    @Builder.Default
    private List<Subtask> subtasks = new ArrayList<>();

    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    @Builder.Default
    private int timeTaken = 0;   // minutes
}
