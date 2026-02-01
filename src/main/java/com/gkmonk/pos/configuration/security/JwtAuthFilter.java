package com.gkmonk.pos.configuration.security;


import com.gkmonk.pos.model.taskmgt.User;
import com.gkmonk.pos.repo.taskmgt.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring("Bearer ".length()).trim();
        try {
            Jws<Claims> parsed = jwtService.parse(token);
            String username = parsed.getPayload().get("sub", String.class);
            if (username == null) {
                response.sendError(401, "Could not validate credentials");
                return;
            }

            User user = userRepo.findByUsername(username).orElse(null);
            if (user == null) {
                response.sendError(401, "Could not validate credentials");
                return;
            }

            if (!user.isApproved()) {
                response.sendError(403, "Account pending admin approval");
                return;
            }

            UserPrincipal principal = new UserPrincipal(user);
            var authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.authorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(401, "Could not validate credentials");
        }
    }
}