package com.revconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage() != null ? ex.getMessage() : "An error occurred");
        error.put("error", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage() != null ? ex.getMessage() : "Runtime error occurred");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
