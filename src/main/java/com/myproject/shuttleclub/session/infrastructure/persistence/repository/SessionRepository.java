package com.myproject.shuttleclub.session.infrastructure.persistence.repository;

import com.myproject.shuttleclub.session.infrastructure.persistence.entity.SessionEntity;
import com.myproject.shuttleclub.session.infrastructure.persistence.entity.SessionStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    Page<SessionEntity> findByClubIdAndStartTimeBetween(
        UUID clubId,
        OffsetDateTime from,
        OffsetDateTime to,
        Pageable pageable
    );

    Page<SessionEntity> findByClubId(UUID clubId, Pageable pageable);
    
    Page<SessionEntity> findByClubIdAndStatus(UUID clubId, SessionStatus status, Pageable pageable);

    Page<SessionEntity> findByClubIdAndStatusAndStartTimeBetween(
        UUID clubId,
        SessionStatus status,
        OffsetDateTime from,
        OffsetDateTime to,
        Pageable pageable
    );
}
