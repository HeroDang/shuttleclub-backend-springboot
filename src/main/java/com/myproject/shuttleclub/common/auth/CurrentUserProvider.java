package com.myproject.shuttleclub.common.auth;

import java.util.UUID;

public interface CurrentUserProvider {
    UUID currentUserId();
}
