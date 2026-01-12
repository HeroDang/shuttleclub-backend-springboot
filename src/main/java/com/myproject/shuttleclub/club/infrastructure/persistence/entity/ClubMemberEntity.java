package com.myproject.shuttleclub.club.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "club_members",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_club_members_club_user",
            columnNames = {"club_id", "user_id"}
        )
    },
    indexes = {
        @Index(name = "idx_club_members_club", columnList = "club_id"),
        @Index(name = "idx_club_members_user", columnList = "user_id")
    }
)
public class ClubMemberEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ClubRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MembershipStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_id")
    private UserEntity invitedBy;

    @Column(name = "joined_at")
    private OffsetDateTime joinedAt;

    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    protected ClubMemberEntity() {}

    public static ClubMemberEntity ownerActive(ClubEntity club, UserEntity user) {
        ClubMemberEntity m = new ClubMemberEntity();
        m.club = club;
        m.user = user;
        m.role = ClubRole.OWNER;
        m.status = MembershipStatus.ACTIVE;
        m.joinedAt = OffsetDateTime.now();
        return m;
    }

    public static ClubMemberEntity invited(ClubEntity club, UserEntity invitee, UserEntity inviter, ClubRole role) {
        ClubMemberEntity m = new ClubMemberEntity();
        m.club = club;
        m.user = invitee;
        m.invitedBy = inviter;
        m.role = role;
        m.status = MembershipStatus.INVITED;
        return m;
    }

    public void acceptInvite() {
        if (this.status != MembershipStatus.INVITED) {
            throw new IllegalStateException("Only INVITED membership can be accepted");
        }
        this.status = MembershipStatus.ACTIVE;
        this.joinedAt = OffsetDateTime.now();
    }

    public void leave() {
        if (this.status != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE membership can leave");
        }
        this.status = MembershipStatus.LEFT;
        this.leftAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public ClubEntity getClub() { return club; }
    public UserEntity getUser() { return user; }
    public ClubRole getRole() { return role; }
    public MembershipStatus getStatus() { return status; }
    public UserEntity getInvitedBy() { return invitedBy; }
    public OffsetDateTime getJoinedAt() { return joinedAt; }
    public OffsetDateTime getLeftAt() { return leftAt; }

    public void setRole(ClubRole role) { this.role = role; }
    public void setStatus(MembershipStatus status) { this.status = status; }
}
