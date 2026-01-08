package com.myproject.shuttleclub.common.api;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

  @GetMapping("/health")
  public ApiResponse<Map<String, Object>> health() {
	    return ApiResponse.ok(Map.of("status", "ok"), MDC.get("requestId"));
  }
}
