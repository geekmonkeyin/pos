package com.gkmonk.pos.model.taskmgt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

public class AuthDtos {

    @Getter @Setter
    public static class LoginRequest {
        @NotBlank public String username;
        @NotBlank public String password;
    }

    @Getter @Setter
    public static class SignupRequest {
        @NotBlank public String username;
        @Email @NotBlank public String email;
        @NotBlank public String full_name;
        @NotBlank public String password;
    }

    @Getter @Setter
    public static class AdminCreateUserRequest {
        @NotBlank public String username;
        @Email @NotBlank public String email;
        @NotBlank public String fullName;
        @NotBlank public String password;
        @NotBlank public String role; // admin/manager/member
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class UserResponse {
        public String id;
        public String username;
        public String email;
        public String fullName;
        public String role;
        public boolean approved;
        public Map<String, Object> page_permissions;
        public String createdAt;
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class TokenResponse {
        public String access_token;
        public String token_type;
        public UserResponse user;
    }
}