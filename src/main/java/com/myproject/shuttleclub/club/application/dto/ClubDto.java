package com.myproject.shuttleclub.club.application.dto;

import java.util.UUID;

public record ClubDto(UUID id, String name, UUID createdByUserId) {}
