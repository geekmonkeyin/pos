package com.gkmonk.pos.model.taskmgt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MiscDtos {

    @Getter @Setter
    public static class PriorityCreateRequest {
        @NotBlank public String name;
        @NotBlank public String color;
        public int order;
    }

    @Getter @Setter
    public static class TeamTypeCreateRequest {
        @NotBlank public String name;
        @NotBlank public String color;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class PerformanceMetric {
        public String userId;
        public String username;
        public String full_name;
        public int tasksCompleted;
        public int totalTime;
    }
}
