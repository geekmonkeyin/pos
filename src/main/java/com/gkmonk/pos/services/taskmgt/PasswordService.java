package com.gkmonk.pos.services.taskmgt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

    private String prehash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString(); // 64 chars
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    public String hash(String password) {
        return enc.encode(prehash(password));
    }

    public boolean verify(String plain, String hashed) {
        return enc.matches(prehash(plain), hashed);
    }
}
