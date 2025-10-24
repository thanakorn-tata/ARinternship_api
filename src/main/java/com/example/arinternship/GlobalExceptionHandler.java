package com.example.arinternship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * จัดการ Validation Errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "ข้อมูลไม่ถูกต้อง");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());

        log.error("Validation error: {}", errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * จัดการ Runtime Exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());

        log.error("Runtime exception: ", ex);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * จัดการ Generic Exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "เกิดข้อผิดพลาดภายในระบบ");
        response.put("timestamp", LocalDateTime.now());
        
        // Log รายละเอียด error แต่ไม่ส่งกลับไปให้ client
        log.error("Unexpected error occurred: ", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * จัดการ SQL/Database Exceptions
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        
        String message = "ข้อมูลซ้ำในระบบ";
        if (ex.getMessage().contains("username")) {
            message = "ชื่อผู้ใช้นี้มีอยู่แล้ว";
        } else if (ex.getMessage().contains("email")) {
            message = "อีเมลนี้มีอยู่แล้ว";
        }
        
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());

        log.error("Data integrity violation: ", ex);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * จัดการ Authentication Exceptions
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
        response.put("timestamp", LocalDateTime.now());

        log.error("Bad credentials: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * จัดการ User Not Found Exception
     */
    @ExceptionHandler(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(
            org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "ไม่พบผู้ใช้ในระบบ");
        response.put("timestamp", LocalDateTime.now());

        log.error("Username not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}