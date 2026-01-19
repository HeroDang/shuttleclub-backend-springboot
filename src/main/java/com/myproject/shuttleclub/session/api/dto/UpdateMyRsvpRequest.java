package com.myproject.shuttleclub.session.api.dto;

import com.myproject.shuttleclub.session.infrastructure.persistence.entity.RsvpStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateMyRsvpRequest(
    @NotNull RsvpStatus status
) {}
