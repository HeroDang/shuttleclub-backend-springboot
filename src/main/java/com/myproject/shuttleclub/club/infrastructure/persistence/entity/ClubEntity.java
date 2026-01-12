package com.myproject.shuttleclub.club.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "clubs",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_clubs_creator_name",
            columnNames = {"created_by_user_id", "name"}
        )
    }
)
public class ClubEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdBy;

    protected ClubEntity() {}

    public ClubEntity(String name, UserEntity createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public UserEntity getCreatedBy() { return createdBy; }

    public void setName(String name) { this.name = name; }
    public void setCreatedBy(UserEntity createdBy) { this.createdBy = createdBy; }
}
