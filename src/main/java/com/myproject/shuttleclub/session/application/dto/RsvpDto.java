package com.myproject.shuttleclub.session.application.dto;

import com.myproject.shuttleclub.session.infrastructure.persistence.entity.RsvpStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RsvpDto(
    UUID id,
    UUID sessionId,
    UUID userId,
    RsvpStatus status,
    OffsetDateTime respondedAt
) {}
