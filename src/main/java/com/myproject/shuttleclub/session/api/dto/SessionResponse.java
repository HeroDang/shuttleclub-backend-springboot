package com.myproject.shuttleclub.session.api.dto;

import com.myproject.shuttleclub.session.infrastructure.persistence.entity.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionResponse(
    UUID id,
    UUID clubId,
    String title,
    OffsetDateTime startTime,
    String location,
    String note,
    SessionStatus status,
    UUID createdBy
) {}
