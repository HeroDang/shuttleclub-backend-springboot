package com.myproject.shuttleclub.common.api;

import java.util.List;

public record PagedResponse<T>(
    List<T> items,
    PageInfo pageInfo
) {}
