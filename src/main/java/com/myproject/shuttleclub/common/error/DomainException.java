package com.myproject.shuttleclub.common.error;

import java.util.List;

import com.myproject.shuttleclub.common.api.ApiError;

public class DomainException extends RuntimeException {
  private final String code;
  private final List<ApiError.FieldIssue> details;

  public DomainException(String code, String message) {
    super(message);
    this.code = code;
    this.details = List.of();
  }

  public DomainException(String code, String message, List<ApiError.FieldIssue> details) {
    super(message);
    this.code = code;
    this.details = details == null ? List.of() : details;
  }

  public String getCode() { return code; }
  public List<ApiError.FieldIssue> getDetails() { return details; }
}
