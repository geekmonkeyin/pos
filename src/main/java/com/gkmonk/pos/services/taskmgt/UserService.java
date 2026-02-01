package com.gkmonk.pos.services.taskmgt;

import com.gkmonk.pos.model.taskmgt.User;
import com.gkmonk.pos.repo.taskmgt.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public List<User> pending() {
        return userRepo.findByApprovedFalse();
    }

    public void approve(String userId) {
        User u = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setApproved(true);
        userRepo.save(u);
    }

    public void updatePermissions(String userId, Map<String, Object> permissions) {
        User u = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setPage_permissions(permissions);
        userRepo.save(u);
    }

    public List<User> all() {
        return userRepo.findAll();
    }

    public User get(String userId) {
        return userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateBasic(String userId, String username, String email, String fullName, String role) {
        User u = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setUsername(username);
        u.setEmail(email);
        u.setFull_name(fullName);
        u.setRole(role);
        return userRepo.save(u);
    }

    public void delete(String userId) {
        if ("admin-001".equals(userId)) throw new IllegalArgumentException("Cannot delete default admin user");
        if (!userRepo.existsById(userId)) throw new IllegalArgumentException("User not found");
        userRepo.deleteById(userId);
    }
}