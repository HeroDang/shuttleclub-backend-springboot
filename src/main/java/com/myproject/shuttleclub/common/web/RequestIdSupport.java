package com.myproject.shuttleclub.common.web;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestIdSupport {
  public static final String HEADER = "X-Request-Id";

  private RequestIdSupport() {}

  public static String requestId(HttpServletRequest request) {
    return request.getHeader(HEADER);
  }
}
