package com.myproject.shuttleclub.club.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClubMemberResponse(
    UUID userId,
    String displayName,
    String role,
    String status,
    OffsetDateTime joinedAt
) {}
