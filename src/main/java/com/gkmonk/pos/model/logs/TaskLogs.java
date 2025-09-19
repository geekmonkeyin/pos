package com.gkmonk.pos.model.logs;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
@Data
public class TaskLogs {

    private String taskName;
    private String status;
    private String taskDate;
    private String deviceId;
    private String metaData;
    private String empName;

    // Constructors
    public TaskLogs() {
    }

}
