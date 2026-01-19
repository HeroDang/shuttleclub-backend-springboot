package com.myproject.shuttleclub.club.api;


import com.myproject.shuttleclub.club.api.dto.ClubMemberResponse;
import com.myproject.shuttleclub.club.api.dto.ClubResponse;
import com.myproject.shuttleclub.club.api.dto.CreateClubRequest;
import com.myproject.shuttleclub.club.api.dto.InviteMemberRequest;
import com.myproject.shuttleclub.club.api.dto.JoinClubRequest;
import com.myproject.shuttleclub.club.api.dto.MembershipResponse;
import com.myproject.shuttleclub.club.application.ClubService;
import com.myproject.shuttleclub.club.application.dto.*;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.ClubRole;
import com.myproject.shuttleclub.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/clubs")
public class ClubController {

  private final ClubService clubService;

  public ClubController(ClubService clubService) {
    this.clubService = clubService;
  }

  // =========================
  // Create club
  // =========================
  @PostMapping
  public ApiResponse<ClubResponse> createClub(
      @Valid @RequestBody CreateClubRequest request,
      HttpServletRequest httpRequest
  ) {
    ClubDto club = clubService.createClub(
        request.creatorUserId(),
        request.name()
    );

    return ApiResponse.ok(
        new ClubResponse(
            club.id(),
            club.name(),
            club.createdByUserId()
        ),
        requestId(httpRequest)
    );
  }

  // =========================
  // Invite member
  // =========================
  @PostMapping("/{clubId}/members/invite")
  public ApiResponse<MembershipResponse> inviteMember(
      @PathVariable UUID clubId,
      @Valid @RequestBody InviteMemberRequest request,
      HttpServletRequest httpRequest
  ) {
    ClubRole role = parseRoleOrDefault(request.role());

    MembershipDto m = clubService.inviteMember(
        clubId,
        request.inviterUserId(),
        request.inviteeUserId(),
        role
    );

    return ApiResponse.ok(
        new MembershipResponse(
            m.membershipId(),
            m.clubId(),
            m.userId(),
            m.role().name(),
            m.status().name(),
            m.joinedAt()
        ),
        requestId(httpRequest)
    );
  }

  // =========================
  // Join club (accept invite)
  // =========================
  @PostMapping("/{clubId}/members/join")
  public ApiResponse<MembershipResponse> joinClub(
      @PathVariable UUID clubId,
      @Valid @RequestBody JoinClubRequest request,
      HttpServletRequest httpRequest
  ) {
    MembershipDto m = clubService.joinClub(
        clubId,
        request.userId()
    );

    return ApiResponse.ok(
        new MembershipResponse(
            m.membershipId(),
            m.clubId(),
            m.userId(),
            m.role().name(),
            m.status().name(),
            m.joinedAt()
        ),
        requestId(httpRequest)
    );
  }

  // =========================
  // List members
  // =========================
  @GetMapping("/{clubId}/members")
  public ApiResponse<List<ClubMemberResponse>> listMembers(
      @PathVariable UUID clubId,
      @RequestParam UUID requesterUserId,
      HttpServletRequest httpRequest
  ) {
    List<ClubMemberView> members =
        clubService.listMembers(clubId, requesterUserId);

    List<ClubMemberResponse> data = members.stream()
        .map(m -> new ClubMemberResponse(
            m.userId(),
            m.displayName(),
            m.role().name(),
            m.status().name(),
            m.joinedAt()
        ))
        .toList();

    return ApiResponse.ok(data, requestId(httpRequest));
  }

  // =========================
  // Helpers
  // =========================
  private String requestId(HttpServletRequest request) {
    // RequestIdFilter đã đảm bảo header này luôn tồn tại
    return request.getHeader("X-Request-Id");
  }

  private ClubRole parseRoleOrDefault(String raw) {
    if (raw == null || raw.isBlank()) {
      return ClubRole.MEMBER;
    }

    try {
      ClubRole role = ClubRole.valueOf(raw.trim().toUpperCase(Locale.ROOT));
      // Business decision: không cho invite OWNER
      return (role == ClubRole.OWNER) ? ClubRole.MEMBER : role;
    } catch (IllegalArgumentException e) {
      // MVP choice: fallback MEMBER
      return ClubRole.MEMBER;
    }
  }
}
