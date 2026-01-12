package com.myproject.shuttleclub.club.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateClubRequest(
    @NotNull UUID creatorUserId,
    @NotBlank String name
) {}
