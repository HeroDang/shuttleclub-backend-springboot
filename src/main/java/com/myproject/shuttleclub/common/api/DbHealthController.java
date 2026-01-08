package com.myproject.shuttleclub.common.api;

import org.slf4j.MDC;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DbHealthController {

  private final JdbcTemplate jdbcTemplate;

  public DbHealthController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping("/health/db")
  public ApiResponse<Map<String, Object>> dbHealth() {
	    Integer one = jdbcTemplate.queryForObject("select 1", Integer.class);
	    return ApiResponse.ok(Map.of("db", "ok", "select", one), MDC.get("requestId"));
  }
}
