package com.myproject.shuttleclub.session.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CreateSessionRequest(
    @NotBlank @Size(max = 100) String title,
    @NotNull OffsetDateTime startTime,
    @NotBlank @Size(max = 255) String location,
    @Size(max = 1000) String note
) {}
