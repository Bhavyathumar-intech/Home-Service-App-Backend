package com.example.HomeService.exceptions;

import com.example.HomeService.dto.errorresponsedto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Catches and handles different types of exceptions thrown from REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Builds a standardized error response.
     *
     * @param message Error message to be returned.
     * @param status HTTP status of the error.
     * @param path The URI path where the error occurred.
     * @return ApiErrorResponse containing all error details.
     */
    private ApiErrorResponse buildErrorResponse(String message, HttpStatus status, String path) {
        return new ApiErrorResponse(
                message,
                LocalDateTime.now(),  // Current timestamp
                path,
                status.value()        // HTTP status code
        );
    }

    /**
     * Handles custom ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        log.error("Resource not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI()));
    }

    /**
     * Handles database duplicate key violations (e.g., unique constraint errors).
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateKeyException(DuplicateKeyException exception, HttpServletRequest request) {
        log.error("Duplicate key error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, request.getRequestURI()));
    }

    /**
     * Handles custom DuplicateResourceException when trying to create a resource that already exists.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(DuplicateResourceException exception, HttpServletRequest request) {
        log.error("Duplicate resource: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    /**
     * Handles unauthorized actions performed by the user.
     */
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAction(UnauthorizedActionException exception, HttpServletRequest request) {
        log.warn("Unauthorized action: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI()));
    }

    /**
     * Handles illegal state exceptions which usually indicate logic violations.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
        log.warn("Illegal state: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    /**
     * Handles validation failures for request body parameters.
     * Typically triggered by @Valid or @Validated annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException exception) {
        log.warn("Validation failed: {}", exception.getMessage());
        Map<String, Object> errors = new HashMap<>();

        // Extract field-level errors
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Add additional metadata
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Catch-all handler for unanticipated exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("Something went wrong: " + exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI()));
    }

    /**
     * Handles scenarios where payment update is not allowed due to business rules.
     */
    @ExceptionHandler(PaymentUpdateNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentUpdateNotAllowed(PaymentUpdateNotAllowedException exception, HttpServletRequest request) {
        log.warn("Payment update not allowed: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(HttpMessageNotReadableException exception, HttpServletRequest request) {
        log.warn("Malformed or missing request body: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse("Invalid or missing request body", HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

}
