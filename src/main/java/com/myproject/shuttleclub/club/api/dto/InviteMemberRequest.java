package com.myproject.shuttleclub.club.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InviteMemberRequest(
    @NotNull UUID inviterUserId,
    @NotNull UUID inviteeUserId,
    String role // optional: "MEMBER" / "ADMIN"
) {}
