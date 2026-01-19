package com.myproject.shuttleclub.session.infrastructure.persistence.entity;

import com.myproject.shuttleclub.club.infrastructure.persistence.entity.AuditableEntity;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "session_rsvps",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_session_rsvps_session_user", columnNames = {"session_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_session_rsvps_session", columnList = "session_id"),
        @Index(name = "idx_session_rsvps_user_responded_at", columnList = "user_id, responded_at")
    }
)
public class SessionRsvpEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RsvpStatus status;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    protected SessionRsvpEntity() {}

    public SessionRsvpEntity(UUID id, UUID sessionId, UUID userId, RsvpStatus status, OffsetDateTime respondedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.status = status;
        this.respondedAt = respondedAt;
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public UUID getUserId() { return userId; }
    public RsvpStatus getStatus() { return status; }
    public OffsetDateTime getRespondedAt() { return respondedAt; }

    public void update(RsvpStatus status, OffsetDateTime respondedAt) {
        this.status = status;
        this.respondedAt = respondedAt;
    }
}
