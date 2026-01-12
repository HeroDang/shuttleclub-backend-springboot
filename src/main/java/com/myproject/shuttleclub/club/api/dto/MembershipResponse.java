package com.myproject.shuttleclub.club.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MembershipResponse(
    UUID membershipId,
    UUID clubId,
    UUID userId,
    String role,
    String status,
    OffsetDateTime joinedAt
) {}
