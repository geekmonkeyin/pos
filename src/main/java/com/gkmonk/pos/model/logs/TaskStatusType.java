package com.gkmonk.pos.model.logs;

public enum TaskStatusType {
    START,IN_PROGRESS,COMPLETED,FAILED,RETRYING,SKIPPED,UNKNOWN;

    public static TaskStatusType getStatusByCode(String code) {
        for (TaskStatusType status : TaskStatusType.values()) {
            String statusName = code.replaceAll("-", "_");
            if (status.name().equalsIgnoreCase(statusName)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
