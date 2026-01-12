package com.myproject.shuttleclub.club.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.ClubEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<ClubEntity, UUID> {
    Optional<ClubEntity> findById(UUID id);
}