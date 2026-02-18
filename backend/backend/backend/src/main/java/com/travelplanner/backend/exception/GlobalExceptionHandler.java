package com.travelplanner.backend.exception;

import com.travelplanner.backend.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation exceptions from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Log validation errors
        logger.warn("Validation failed: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, "Validation failed", errors));
    }

    /**
     * Handle user already exists exception
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        logger.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle unauthorized access exceptions
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        logger.warn("Unauthorized access: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException ex) {
        logger.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle data integrity violations (e.g., duplicate entries, foreign key constraints)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage());

        String message = "Database error occurred";

        // Check for specific constraint violations
        if (ex.getMessage().contains("duplicate key")) {
            message = "A record with this information already exists";
        } else if (ex.getMessage().contains("foreign key")) {
            message = "Cannot delete because this record is referenced by other records";
        } else if (ex.getMessage().contains("not null")) {
            message = "Required field is missing";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message));
    }

    /**
     * Handle malformed JSON requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.error("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Invalid request format"));
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        logger.warn("Missing parameter: {}", ex.getMessage());
        String message = String.format("Missing required parameter: %s", ex.getParameterName());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message));
    }

    /**
     * Handle type mismatch (e.g., String instead of Long)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Type mismatch: {}", ex.getMessage());
        String message = String.format("Invalid value for parameter '%s'", ex.getName());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message));
    }

    /**
     * Handle Spring Security access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You do not have permission to access this resource"));
    }

    /**
     * Handle Spring Security authentication exceptions
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleSpringAuthException(
            org.springframework.security.core.AuthenticationException ex) {
        logger.warn("Spring authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required"));
    }

    /**
     * Handle unsupported operations
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiResponse> handleUnsupportedOperation(UnsupportedOperationException ex) {
        logger.warn("Unsupported operation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle any other exception - catch-all
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        // Generate a unique error ID for tracking
        String errorId = UUID.randomUUID().toString();

        // Log the full error with stack trace and error ID
        logger.error("Unhandled exception [ErrorID: {}]: {}", errorId, ex.getMessage(), ex);

        // Return user-friendly message with error ID for support reference
        String message = "An unexpected error occurred. Please try again later. Reference ID: " + errorId;

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message));
    }
}