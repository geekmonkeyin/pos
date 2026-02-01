package com.gkmonk.pos.configuration;


import com.gkmonk.pos.model.taskmgt.Consts;
import com.gkmonk.pos.model.taskmgt.Priority;
import com.gkmonk.pos.model.taskmgt.TeamType;
import com.gkmonk.pos.model.taskmgt.User;
import com.gkmonk.pos.repo.taskmgt.PriorityRepo;
import com.gkmonk.pos.repo.taskmgt.TeamTypeRepo;
import com.gkmonk.pos.repo.taskmgt.UserRepo;
import com.gkmonk.pos.services.taskmgt.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbInitRunner implements ApplicationRunner {

    private final UserRepo userRepo;
    private final PriorityRepo priorityRepo;
    private final TeamTypeRepo teamTypeRepo;
    private final PasswordService passwordService;

    @Override
    public void run(ApplicationArguments args) {
        // Admin
        userRepo.findByUsername("admin").ifPresentOrElse(
                u -> {},
                () -> {
                    User admin = User.builder()
                            .id("admin-001")
                            .username("admin")
                            .passwordHash(passwordService.hash("admin"))
                            .email("admin@taskmanager.com")
                            .full_name("System Administrator")
                            .role(Consts.UserRole.ADMIN)
                            .approved(true)
                            .page_permissions(Map.of(
                                    "dashboard", Map.of("view", true, "edit", true),
                                    "tasks", Map.of("view", true, "edit", true),
                                    "weekly_planning", Map.of("view", true, "edit", true),
                                    "employees", Map.of("view", true, "edit", true),
                                    "settings", Map.of("view", true, "edit", true)
                            ))
                            .createdAt(Instant.now())
                            .build();
                    userRepo.save(admin);
                }
        );

        // Priorities
        if (priorityRepo.count() == 0) {
            priorityRepo.save(Priority.builder().id("priority-1").name("Critical").color("#ef4444").order(1).build());
            priorityRepo.save(Priority.builder().id("priority-2").name("High").color("#f97316").order(2).build());
            priorityRepo.save(Priority.builder().id("priority-3").name("Medium").color("#eab308").order(3).build());
            priorityRepo.save(Priority.builder().id("priority-4").name("Low").color("#22c55e").order(4).build());
        }

        // Team Types
        if (teamTypeRepo.count() == 0) {
            teamTypeRepo.save(TeamType.builder().id("team-1").name("Development").color("#3b82f6").build());
            teamTypeRepo.save(TeamType.builder().id("team-2").name("Marketing").color("#ec4899").build());
            teamTypeRepo.save(TeamType.builder().id("team-3").name("Operations").color("#8b5cf6").build());
        }
    }
}