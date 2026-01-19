package com.myproject.shuttleclub.common.api;

public record PageInfo(
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {}
