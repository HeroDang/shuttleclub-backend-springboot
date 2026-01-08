package com.myproject.shuttleclub.common.api;

import java.util.List;

public record ApiError(
    String code,
    String message,
    List<FieldIssue> details
) {
  public record FieldIssue(String field, String issue) {}
}
