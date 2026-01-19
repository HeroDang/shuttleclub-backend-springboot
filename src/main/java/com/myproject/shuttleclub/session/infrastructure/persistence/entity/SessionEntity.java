package com.myproject.shuttleclub.session.infrastructure.persistence.entity;

import com.myproject.shuttleclub.club.infrastructure.persistence.entity.AuditableEntity;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions",
       indexes = {
           @Index(name = "idx_sessions_club_start_time", columnList = "club_id, start_time, id")
       })
public class SessionEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "club_id", nullable = false)
    private UUID clubId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    protected SessionEntity() {}

    public SessionEntity(UUID id, UUID clubId, String title, OffsetDateTime startTime,
                         String location, String note, SessionStatus status, UUID createdBy) {
        this.id = id;
        this.clubId = clubId;
        this.title = title;
        this.startTime = startTime;
        this.location = location;
        this.note = note;
        this.status = status;
        this.createdBy = createdBy;
    }

    public UUID getId() { return id; }
    public UUID getClubId() { return clubId; }
    public String getTitle() { return title; }
    public OffsetDateTime getStartTime() { return startTime; }
    public String getLocation() { return location; }
    public String getNote() { return note; }
    public SessionStatus getStatus() { return status; }
    public UUID getCreatedBy() { return createdBy; }

    public void close() { this.status = SessionStatus.CLOSED; }
}
