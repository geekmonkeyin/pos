package com.gkmonk.pos.model.logs;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
public class TaskLogs {

    private String taskName;
    private String status;
    private String taskDate;
    private String deviceId;
    private String metaData;

    // Constructors
    public TaskLogs() {
    }


    public String getMetaData() {
        return metaData;
    }
    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getTaskDate() {
        return taskDate;
    }
    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
