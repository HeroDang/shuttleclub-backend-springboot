package com.myproject.shuttleclub.club.application;


import com.myproject.shuttleclub.club.application.dto.ClubDto;
import com.myproject.shuttleclub.club.application.dto.ClubMemberView;
import com.myproject.shuttleclub.club.application.dto.MembershipDto;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.*;
import com.myproject.shuttleclub.club.infrastructure.persistence.repository.*;
import com.myproject.shuttleclub.common.error.ConflictException;
import com.myproject.shuttleclub.common.error.ErrorCodes;
import com.myproject.shuttleclub.common.error.ForbiddenException;
import com.myproject.shuttleclub.common.error.NotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClubService {

  private final UserRepository userRepo;
  private final ClubRepository clubRepo;
  private final ClubMemberRepository memberRepo;

  public ClubService(UserRepository userRepo, ClubRepository clubRepo, ClubMemberRepository memberRepo) {
    this.userRepo = userRepo;
    this.clubRepo = clubRepo;
    this.memberRepo = memberRepo;
  }

  /**
   * Create club + create OWNER membership (ACTIVE).
   * Transaction boundary: REQUIRED (default).
   */
  @Transactional
  public ClubDto createClub(UUID creatorUserId, String name) {
    UserEntity creator = userRepo.findById(creatorUserId)
        .orElseThrow(() -> new NotFoundException(ErrorCodes.USER_NOT_FOUND, "User not found: " + creatorUserId));

    ClubEntity club = new ClubEntity(name, creator);
    clubRepo.save(club);

    // invariant: each club has exactly 1 ACTIVE OWNER
    ClubMemberEntity owner = ClubMemberEntity.ownerActive(club, creator);
    memberRepo.save(owner);

    return new ClubDto(club.getId(), club.getName(), creator.getId());
  }

  /**
   * Invite user to club with role (usually MEMBER).
   * Rules:
   * - inviter must be ACTIVE member with role OWNER/ADMIN
   * - invitee must not already have membership in club
   */
  @Transactional
  public MembershipDto inviteMember(UUID clubId, UUID inviterUserId, UUID inviteeUserId, ClubRole role) {
    ClubEntity club = clubRepo.findById(clubId)
        .orElseThrow(() -> new NotFoundException(ErrorCodes.CLUB_NOT_FOUND, "Club not found: " + clubId));

    UserEntity inviter = userRepo.findById(inviterUserId)
        .orElseThrow(() -> new NotFoundException(ErrorCodes.USER_NOT_FOUND, "Inviter not found: " + inviterUserId));

    UserEntity invitee = userRepo.findById(inviteeUserId)
        .orElseThrow(() -> new NotFoundException(ErrorCodes.USER_NOT_FOUND, "Invitee not found: " + inviteeUserId));

    ClubMemberEntity inviterMembership = memberRepo.findActiveMember(clubId, inviterUserId)
        .orElseThrow(() -> new ForbiddenException("Inviter is not an ACTIVE member of this club"));

    if (!(inviterMembership.getRole() == ClubRole.OWNER || inviterMembership.getRole() == ClubRole.ADMIN)) {
      throw new ForbiddenException("Only OWNER/ADMIN can invite members");
    }

    if (memberRepo.existsByClub_IdAndUser_Id(clubId, inviteeUserId)) {
      throw new ConflictException(ErrorCodes.MEMBERSHIP_ALREADY_EXISTS, "User already has membership in this club");
    }

    ClubRole targetRole = (role == null) ? ClubRole.MEMBER : role;
    // business choice: do not allow inviting OWNER
    if (targetRole == ClubRole.OWNER) {
      throw new ConflictException(ErrorCodes.INVALID_MEMBERSHIP_STATE, "Cannot invite user as OWNER");
    }

    ClubMemberEntity invited = ClubMemberEntity.invited(club, invitee, inviter, targetRole);
    memberRepo.save(invited);

    return new MembershipDto(invited.getId(), clubId, inviteeUserId, invited.getRole(), invited.getStatus(), invited.getJoinedAt());
  }

  /**
   * Accept invite: INVITED -> ACTIVE
   */
  @Transactional
  public MembershipDto joinClub(UUID clubId, UUID userId) {
    // must exist & be INVITED
    ClubMemberEntity m = memberRepo.findInvitedMember(clubId, userId)
        .orElseThrow(() -> new NotFoundException(ErrorCodes.MEMBERSHIP_NOT_FOUND, "No INVITED membership found to join"));

    try {
      m.acceptInvite();
    } catch (IllegalStateException e) {
      throw new ConflictException(ErrorCodes.INVALID_MEMBERSHIP_STATE, e.getMessage());
    }

    // JPA dirty checking will flush update in txn
    return new MembershipDto(m.getId(), clubId, userId, m.getRole(), m.getStatus(), m.getJoinedAt());
  }

  /**
   * List members (with user display name)
   * Rule (MVP): requester must be ACTIVE member
   */
  @Transactional(readOnly = true)
  public List<ClubMemberView> listMembers(UUID clubId, UUID requesterUserId) {
    // permission gate
    memberRepo.findActiveMember(clubId, requesterUserId)
        .orElseThrow(() -> new ForbiddenException("Only ACTIVE members can view club members"));

    // fetch join to avoid N+1
    return memberRepo.findMembersWithUser(clubId).stream()
        .map(m -> new ClubMemberView(
            m.getUser().getId(),
            m.getUser().getDisplayName(),
            m.getRole(),
            m.getStatus(),
            m.getJoinedAt()
        ))
        .toList();
  }
}
