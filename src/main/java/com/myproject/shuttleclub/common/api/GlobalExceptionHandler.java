package com.myproject.shuttleclub.common.api;

import com.myproject.shuttleclub.common.exception.DomainException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
    var details = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> new ApiError.FieldIssue(fe.getField(), fe.getDefaultMessage()))
        .toList();

    var err = new ApiError("VALIDATION_ERROR", "Request validation failed", details);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(err, requestId()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
    var details = ex.getConstraintViolations().stream()
        .map(v -> new ApiError.FieldIssue(v.getPropertyPath().toString(), v.getMessage()))
        .toList();

    var err = new ApiError("VALIDATION_ERROR", "Request validation failed", details);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(err, requestId()));
  }

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiResponse<Void>> handleDomain(DomainException ex) {
    var err = new ApiError(ex.code(), ex.getMessage(), List.of());
    return ResponseEntity.status(ex.status()).body(ApiResponse.error(err, requestId()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
    var err = new ApiError("INTERNAL_ERROR", "Unexpected error", List.of());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(err, requestId()));
  }

  private String requestId() {
    String id = MDC.get("requestId");
    return (id != null && !id.isBlank()) ? id : "unknown";
  }
}
