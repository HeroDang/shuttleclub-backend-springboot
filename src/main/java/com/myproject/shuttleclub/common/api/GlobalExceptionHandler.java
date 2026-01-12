package com.myproject.shuttleclub.common.api;


import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import com.myproject.shuttleclub.common.error.DomainException;

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
  public ResponseEntity<ApiError> handleDomain(DomainException ex, HttpServletRequest req) {
    HttpStatus status = mapStatus(ex.getCode());
    ApiError body = new ApiError(ex.getCode(), ex.getMessage(), ex.getDetails());
    return ResponseEntity.status(status).body(body);
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
  
  private HttpStatus mapStatus(String code) {
	    return switch (code) {
	      case com.myproject.shuttleclub.common.error.ErrorCodes.USER_NOT_FOUND,
	           com.myproject.shuttleclub.common.error.ErrorCodes.CLUB_NOT_FOUND,
	           com.myproject.shuttleclub.common.error.ErrorCodes.MEMBERSHIP_NOT_FOUND
	           -> HttpStatus.NOT_FOUND;

	      case com.myproject.shuttleclub.common.error.ErrorCodes.MEMBERSHIP_ALREADY_EXISTS
	           -> HttpStatus.CONFLICT;

	      case com.myproject.shuttleclub.common.error.ErrorCodes.FORBIDDEN
	           -> HttpStatus.FORBIDDEN;

	      case com.myproject.shuttleclub.common.error.ErrorCodes.MEMBERSHIP_NOT_INVITED,
	           com.myproject.shuttleclub.common.error.ErrorCodes.INVALID_MEMBERSHIP_STATE
	           -> HttpStatus.BAD_REQUEST;

	      default -> HttpStatus.BAD_REQUEST;
	    };
	  }
}
