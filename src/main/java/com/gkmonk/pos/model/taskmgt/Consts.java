package com.gkmonk.pos.model.taskmgt;

public final class Consts {
    private Consts() {}

    public static final class UserRole {
        public static final String ADMIN = "admin";
        public static final String MANAGER = "manager";
        public static final String MEMBER = "member";
    }

    public static final class TaskStatus {
        public static final String TODO = "todo";
        public static final String IN_PROGRESS = "in_progress";
        public static final String COMPLETED = "completed";
        public static final String BLOCKED = "blocked";
    }
}
