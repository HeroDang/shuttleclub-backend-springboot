package com.myproject.shuttleclub.club.infrastructure.persistence.repository;


import com.myproject.shuttleclub.club.infrastructure.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubMemberRepository extends JpaRepository<ClubMemberEntity, UUID> {

    Optional<ClubMemberEntity> findByClub_IdAndUser_Id(UUID clubId, UUID userId);

    List<ClubMemberEntity> findAllByClub_Id(UUID clubId);

    List<ClubMemberEntity> findAllByClub_IdAndStatus(UUID clubId, MembershipStatus status);

    boolean existsByClub_IdAndUser_Id(UUID clubId, UUID userId);

    @Query("""
        select m
        from ClubMemberEntity m
        where m.club.id = :clubId
          and m.user.id = :userId
          and m.status = 'ACTIVE'
    """)
    Optional<ClubMemberEntity> findActiveMember(UUID clubId, UUID userId);

    @Query("""
        select m
        from ClubMemberEntity m
        where m.club.id = :clubId
          and m.user.id = :userId
          and m.status = 'INVITED'
    """)
    Optional<ClubMemberEntity> findInvitedMember(UUID clubId, UUID userId);

    @Query("""
        select m
        from ClubMemberEntity m
        join fetch m.user u
        where m.club.id = :clubId
        order by 
          case m.role when 'OWNER' then 0 when 'ADMIN' then 1 else 2 end,
          u.displayName asc
    """)
    List<ClubMemberEntity> findMembersWithUser(UUID clubId);
}