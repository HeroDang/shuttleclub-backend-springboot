package com.myproject.shuttleclub.common.auth;

import com.myproject.shuttleclub.common.error.ForbiddenException;
import com.myproject.shuttleclub.common.error.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HeaderCurrentUserProvider implements CurrentUserProvider {

    private static final String HEADER = "X-User-Id";

    private final HttpServletRequest request;

    public HeaderCurrentUserProvider(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public UUID currentUserId() {
        String raw = request.getHeader(HEADER);
        if (raw == null || raw.isBlank()) {
            // tạm thời dùng Forbidden để khỏi phải thêm UnauthorizedException
            throw new ForbiddenException(ErrorCodes.AUTH_REQUIRED, "Missing header " + HEADER);
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException(ErrorCodes.AUTH_INVALID, "Invalid UUID in header " + HEADER);
        }
    }
}
