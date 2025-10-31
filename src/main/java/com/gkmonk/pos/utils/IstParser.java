package com.gkmonk.pos.utils;

import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class IstParser {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    // Typical Intl.DateTimeFormat('en-IN', { ... }) → "dd/MM/yyyy, hh:mm:ss a"
    private static final DateTimeFormatter EN_IN =
            DateTimeFormatter.ofPattern("dd/MM/uuuu, hh:mm:ss a", Locale.ENGLISH);

    public static Instant parseIstToInstant(String s) {
        if (!StringUtils.hasText(s)) {
            return ZonedDateTime.now(IST).toInstant();
        }
        s = s.trim();
        // Try en-IN first
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, EN_IN);
            return ldt.atZone(IST).toInstant();
        } catch (DateTimeParseException ignore) {}
        // Try ISO local date-time
        try {
            LocalDateTime ldt = LocalDateTime.parse(s);
            return ldt.atZone(IST).toInstant();
        } catch (DateTimeParseException ignore) {}
        // Try instant (ISO instant)
        try {
            return Instant.parse(s);
        } catch (DateTimeParseException ignore) {}
        // Fallback: now(IST)
        return ZonedDateTime.now(IST).toInstant();
    }
}
