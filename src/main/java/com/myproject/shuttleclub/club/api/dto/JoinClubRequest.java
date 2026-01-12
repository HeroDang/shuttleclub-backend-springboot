package com.myproject.shuttleclub.club.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JoinClubRequest(
    @NotNull UUID userId
) {}
