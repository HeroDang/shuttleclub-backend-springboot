package com.myproject.shuttleclub.group.api;

import com.myproject.shuttleclub.common.api.ApiResponse;
import com.myproject.shuttleclub.common.exception.DomainException;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class GroupController {

  @PostMapping
  public ApiResponse<Map<String, Object>> create(@Valid @RequestBody CreateGroupRequest req) {
    // Tuần 1: chưa có DB => trả mock id
    String id = UUID.randomUUID().toString();
    return ApiResponse.ok(Map.of("id", id, "name", req.name()), MDC.get("requestId"));
  }

  @GetMapping("/{groupId}")
  public ApiResponse<Map<String, Object>> get(@PathVariable String groupId) {
    // Tuần 1: demo 404 business
    throw DomainException.notFound("GROUP_NOT_FOUND", "Group not found: " + groupId);
  }
}
