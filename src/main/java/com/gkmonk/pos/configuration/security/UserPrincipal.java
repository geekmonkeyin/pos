package com.gkmonk.pos.configuration.security;

import com.gkmonk.pos.model.taskmgt.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter @AllArgsConstructor
public class UserPrincipal {
    private final User user;

    public List<GrantedAuthority> authorities() {
        // We map to ROLE_ADMIN / ROLE_MANAGER / ROLE_MEMBER
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
    }
}