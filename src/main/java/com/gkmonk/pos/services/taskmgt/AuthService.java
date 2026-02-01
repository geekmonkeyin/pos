package com.gkmonk.pos.services.taskmgt;


import com.gkmonk.pos.configuration.security.JwtService;
import com.gkmonk.pos.model.taskmgt.AuthDtos;
import com.gkmonk.pos.model.taskmgt.Consts;
import com.gkmonk.pos.model.taskmgt.User;
import com.gkmonk.pos.repo.taskmgt.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest req) {
        User user = userRepo.findByUsername(req.username).orElse(null);
        if (user == null || !passwordService.verify(req.password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!user.isApproved()) {
            throw new SecurityException("Account pending admin approval");
        }

        String token = jwtService.createToken(Map.of("sub", user.getUsername()));
        return AuthDtos.TokenResponse.builder()
                .access_token(token)
                .token_type("bearer")
                .user(toUserResponse(user))
                .build();
    }

    public void signup(AuthDtos.SignupRequest req) {
        if (userRepo.findByUsername(req.username).isPresent()) throw new IllegalArgumentException("Username already exists");
        if (userRepo.findByEmail(req.email).isPresent()) throw new IllegalArgumentException("Email already exists");

        Instant now = Instant.now();
        User u = User.builder()
                .id("user-" + UUID.randomUUID())
                .username(req.username)
                .email(req.email)
                .full_name(req.full_name)
                .passwordHash(passwordService.hash(req.password))
                .role(Consts.UserRole.MEMBER)
                .approved(false)
                .page_permissions(Map.of(
                        "dashboard", Map.of("view", true, "edit", false),
                        "tasks", Map.of("view", true, "edit", false),
                        "weekly_planning", Map.of("view", true, "edit", false),
                        "employees", Map.of("view", false, "edit", false),
                        "settings", Map.of("view", false, "edit", false)
                ))
                .createdAt(now)
                .build();

        userRepo.save(u);
    }

    public AuthDtos.UserResponse adminRegister(AuthDtos.AdminCreateUserRequest req) {
        if (userRepo.findByUsername(req.username).isPresent()) throw new IllegalArgumentException("Username already exists");
        if (userRepo.findByEmail(req.email).isPresent()) throw new IllegalArgumentException("Email already exists");

        Instant now = Instant.now();
        boolean viewEmployees = req.role.equals(Consts.UserRole.ADMIN) || req.role.equals(Consts.UserRole.MANAGER);
        boolean editEmployees = req.role.equals(Consts.UserRole.ADMIN);

        User u = User.builder()
                .id("user-" + UUID.randomUUID())
                .username(req.username)
                .email(req.email)
                .full_name(req.fullName)
                .passwordHash(passwordService.hash(req.password))
                .role(req.role)
                .approved(true)
                .page_permissions(Map.of(
                        "dashboard", Map.of("view", true, "edit", true),
                        "tasks", Map.of("view", true, "edit", true),
                        "weekly_planning", Map.of("view", true, "edit", true),
                        "employees", Map.of("view", viewEmployees, "edit", editEmployees),
                        "settings", Map.of("view", editEmployees, "edit", editEmployees)
                ))
                .createdAt(now)
                .build();

        userRepo.save(u);
        return toUserResponse(u);
    }

    private AuthDtos.UserResponse toUserResponse(User u) {
        return AuthDtos.UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .fullName(u.getFull_name())
                .role(u.getRole())
                .approved(u.isApproved())
                .page_permissions(u.getPage_permissions())
                .createdAt(DateTimeFormatter.ISO_INSTANT.format(u.getCreatedAt()))
                .build();
    }
}
