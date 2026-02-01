package com.gkmonk.pos.model.taskmgt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subtask {
    private String id;
    private String title;
    @Builder.Default
    private boolean completed = false;
}