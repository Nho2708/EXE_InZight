package org.inzight.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException ex) {
        ErrorCode error = ex.getErrorCode();

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", error.getStatusCode().value());
        body.put("error", error.getStatusCode().toString());
        body.put("code", error.getCode());
        body.put("message", error.getMessage());

        return ResponseEntity.status(error.getStatusCode().value()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("code", ErrorCode.UNCATEGORIES_EXCEPTION.getCode());
        body.put("message", ex.getMessage());

        return ResponseEntity.status(500).body(body);
    }
}