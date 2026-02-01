package com.gkmonk.pos.controller.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalAiExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalAiExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception ex) {
        String errorId = UUID.randomUUID().toString();
        log.error("[ERROR][{}] Unhandled exception", errorId, ex);

        return ResponseEntity.internalServerError().body(Map.of(
                "errorId", errorId,
                "message", "AI processing failed. Check logs."
        ));
    }
}
