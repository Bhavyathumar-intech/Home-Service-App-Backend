//package com.example.HomeService.exceptions;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException exception) {
//        Map<String, Object> error = new HashMap<>();
//        log.error(exception.getMessage());
//        error.put("error", exception.getMessage());
//        error.put("timestamp", LocalDateTime.now());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//    }
//
//    @ExceptionHandler(DuplicateKeyException.class)
//    public ResponseEntity<?> duplicateKeyException(DuplicateKeyException exception) {
//        log.error(exception.getMessage());
//        Map<String, Object> error = new HashMap<>();
//        error.put("error", exception.getMessage());
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGenericException(Exception ex) {
//        return new ResponseEntity<>("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(DuplicateResourceException.class)
//    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(UnauthorizedActionException.class)
//    public ResponseEntity<String> handleUnauthorizedAction(UnauthorizedActionException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN); // 403 Forbidden
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
//}
//

package com.example.HomeService.exceptions;

import com.example.HomeService.dto.errorresponsedto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Common method to create consistent error response
    private ApiErrorResponse buildErrorResponse(String message, HttpStatus status, String path) {
        return new ApiErrorResponse(
                message,
                LocalDateTime.now(),
                path,
                status.value()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        log.error("Resource not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateKeyException(DuplicateKeyException exception, HttpServletRequest request) {
        log.error("Duplicate key error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, request.getRequestURI()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(DuplicateResourceException exception, HttpServletRequest request) {
        log.error("Duplicate resource: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAction(UnauthorizedActionException exception, HttpServletRequest request) {
        log.warn("Unauthorized action: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
        log.warn("Illegal state: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException exception) {
        log.warn("Validation failed: {}", exception.getMessage());
        Map<String, Object> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("Something went wrong: " + exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI()));
    }
}
