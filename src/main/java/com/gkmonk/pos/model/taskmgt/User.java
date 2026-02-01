package com.gkmonk.pos.model.taskmgt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document("users")
public class User {
    @Id
    private String id;

    private String username;
    private String email;
    private String full_name;

    private String role;      // admin/manager/member
    private boolean approved; // pending admin approval

    private String passwordHash;

    @Builder.Default
    private Map<String, Object> page_permissions = new HashMap<>();

    private Instant createdAt;
}