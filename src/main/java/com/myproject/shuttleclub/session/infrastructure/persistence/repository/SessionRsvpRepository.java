package com.myproject.shuttleclub.session.infrastructure.persistence.repository;

import com.myproject.shuttleclub.session.infrastructure.persistence.entity.SessionRsvpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessionRsvpRepository extends JpaRepository<SessionRsvpEntity, UUID> {
    Optional<SessionRsvpEntity> findBySessionIdAndUserId(UUID sessionId, UUID userId);
}
