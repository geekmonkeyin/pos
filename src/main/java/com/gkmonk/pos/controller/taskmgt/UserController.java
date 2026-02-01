package com.gkmonk.pos.controller.taskmgt;


import com.gkmonk.pos.model.taskmgt.User;
import com.gkmonk.pos.services.taskmgt.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> pending() {
        return userService.pending();
    }

    @PostMapping("/users/{userId}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, String> approve(@PathVariable String userId) {
        userService.approve(userId);
        return Map.of("message", "User approved successfully");
    }

    @PutMapping("/users/{userId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, String> permissions(@PathVariable String userId, @RequestBody Map<String, Object> permissions) {
        userService.updatePermissions(userId, permissions);
        return Map.of("message", "Permissions updated successfully");
    }

    @GetMapping("/users")
    public List<User> users() {
        return userService.all();
    }

    @GetMapping("/users/{userId}")
    public User user(@PathVariable String userId) {
        return userService.get(userId);
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public User update(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest req) {
        return userService.updateBasic(userId, req.username, req.email, req.fullName, req.role);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String userId) {
        try {
            userService.delete(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("detail", e.getMessage()));
        }
    }

    @Getter @Setter
    public static class UpdateUserRequest {
        public String username;
        public String email;
        public String fullName;
        public String role;
    }
}