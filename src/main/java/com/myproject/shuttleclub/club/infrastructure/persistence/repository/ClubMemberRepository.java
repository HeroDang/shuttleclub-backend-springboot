package com.myproject.shuttleclub.club.infrastructure.persistence.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myproject.shuttleclub.club.infrastructure.persistence.entity.ClubMemberEntity;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.ClubRole;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.MembershipStatus;

import java.util.Collection;
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
    
    boolean existsByClubIdAndUserIdAndStatus(UUID clubId, UUID userId, MembershipStatus status);

    boolean existsByClubIdAndUserIdAndStatusAndRoleIn(
        UUID clubId,
        UUID userId,
        MembershipStatus status,
        java.util.Collection<ClubRole> roles
    );
    
    @Query("""
    	      select (count(m) > 0)
    	      from ClubMemberEntity m
    	      where m.club.id = :clubId
    	        and m.user.id = :userId
    	        and m.status = :status
    	      """)
    	  boolean existsMember(
    	      @Param("clubId") UUID clubId,
    	      @Param("userId") UUID userId,
    	      @Param("status") MembershipStatus status
    	  );

    @Query("""
    	      select (count(m) > 0)
    	      from ClubMemberEntity m
    	      where m.club.id = :clubId
    	        and m.user.id = :userId
    	        and m.status = :status
    	        and m.role in :roles
    	      """)
    	  boolean existsMemberWithRoles(
    	      @Param("clubId") UUID clubId,
    	      @Param("userId") UUID userId,
    	      @Param("status") MembershipStatus status,
    	      @Param("roles") Collection<ClubRole> roles
    	  );
    
}