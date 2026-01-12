package com.myproject.shuttleclub.club.api.dto;

import java.util.UUID;

public record ClubResponse(
    UUID id,
    String name,
    UUID createdByUserId
) {}
