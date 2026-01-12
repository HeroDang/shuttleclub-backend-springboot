package com.myproject.shuttleclub.club.application.dto;

import com.myproject.shuttleclub.club.infrastructure.persistence.entity.ClubRole;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.MembershipStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClubMemberView(
    UUID userId,
    String displayName,
    ClubRole role,
    MembershipStatus status,
    OffsetDateTime joinedAt
) {}
