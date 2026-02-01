package com.gkmonk.pos.model.taskmgt;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class TaskDtos {

    @Getter @Setter
    public static class TaskCreateRequest {
        @NotBlank public String title;
        public String description;
        public String priorityId;
        public String teamTypeId;
        public String status;     // optional; default in service
        public String assignedTo;
        public String dueDate;
    }

    @Getter @Setter
    public static class TaskUpdateRequest {
        public String title;
        public String description;
        public String priorityId;
        public String teamTypeId;
        public String status;
        public String assignedTo;
        public String dueDate;
        public Integer timeTaken;
    }

    @Getter @Setter
    public static class SubtaskCreateRequest {
        @NotBlank public String title;
    }

    @Getter @Setter
    public static class SubtaskUpdateRequest {
        public boolean completed;
    }
}
