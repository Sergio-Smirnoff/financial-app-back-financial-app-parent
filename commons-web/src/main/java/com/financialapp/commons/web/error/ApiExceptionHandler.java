package com.financialapp.commons.web.error;

import com.financialapp.commons.core.error.DomainException;
import com.financialapp.commons.core.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    protected Map<String, String> constraintMessages() {
        return Map.of();
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDomain(DomainException ex) {
        HttpStatus status = ErrorCategoryHttpMapper.toHttpStatus(ex.getError().category());
        log.warn("Domain error [{}]: {}", ex.getError().code(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(ApiResponse.failure(status, ex.getError().code(), ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        log.warn("Validation failed: {}", fields);
        return ResponseEntity.badRequest().body(ApiResponse.failure(
                HttpStatus.BAD_REQUEST, CommonErrorCode.VALIDATION_ERROR.code(),
                "Request validation failed", fields));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.failure(
                HttpStatus.BAD_REQUEST, CommonErrorCode.VALIDATION_ERROR.code(), ex.getMessage(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed request body: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.failure(
                HttpStatus.BAD_REQUEST, CommonErrorCode.MALFORMED_REQUEST.code(),
                "Malformed or invalid request body", null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Throwable mostSpecific = ex.getMostSpecificCause();
        String sqlState = (mostSpecific instanceof SQLException sqlEx) ? sqlEx.getSQLState() : null;
        String cause = mostSpecific.getMessage();

        if ("23502".equals(sqlState) || "23514".equals(sqlState)) {
            String column = extractColumn(cause);
            log.warn("Constraint violation [{}] on column [{}]", sqlState, column);
            return ResponseEntity.badRequest().body(ApiResponse.failure(
                    HttpStatus.BAD_REQUEST, CommonErrorCode.VALIDATION_ERROR.code(),
                    "Invalid value for column '" + column + "'", Map.of("column", column)));
        }

        String constraint = constraintMessages().keySet().stream()
                .filter(constraintKey -> cause != null && cause.contains(constraintKey))
                .findFirst().orElse("unknown_constraint");
        String message = constraintMessages().getOrDefault(constraint, "Data conflict");
        log.warn("Data integrity violation [{}]", constraint);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.failure(
                HttpStatus.CONFLICT, CommonErrorCode.DATABASE_CONFLICT.code(),
                message, Map.of("constraint", constraint)));
    }

    private static String extractColumn(String causeMessage) {
        if (causeMessage == null) return "unknown";
        Matcher matcher = Pattern
                .compile("column \"([^\"]+)\"").matcher(causeMessage);
        return matcher.find() ? matcher.group(1) : "unknown";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError().body(ApiResponse.failure(
                HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCode.INTERNAL_ERROR.code(),
                "An unexpected error occurred", null));
    }
}
