package com.myproject.shuttleclub.club.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "uq_users_email", columnList = "email", unique = true),
        @Index(name = "uq_users_phone", columnList = "phone", unique = true)
    }
)
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    // Note: created_at/updated_at are in DB. If you prefer, add fields like in AuditableEntity.
    // For now, keep minimal mapping to pass validate without needing auditing config.

    protected UserEntity() {}

    public UserEntity(String displayName, String email, String phone) {
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.status = UserStatus.ACTIVE;
    }

    public UUID getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public UserStatus getStatus() { return status; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(UserStatus status) { this.status = status; }
}
