package com.myproject.shuttleclub.common.api;

public record ApiResponse<T>(
    boolean success,
    T data,
    ApiError error,
    ApiMeta meta
) {
  public static <T> ApiResponse<T> ok(T data, String requestId) {
    return new ApiResponse<>(true, data, null, new ApiMeta(requestId));
  }

  public static ApiResponse<Void> error(ApiError error, String requestId) {
    return new ApiResponse<>(false, null, error, new ApiMeta(requestId));
  }

  public record ApiMeta(String requestId) {}
}
