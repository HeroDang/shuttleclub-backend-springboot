package com.myproject.shuttleclub.session.application;

import com.myproject.shuttleclub.club.infrastructure.persistence.entity.ClubRole;
import com.myproject.shuttleclub.club.infrastructure.persistence.entity.MembershipStatus;
import com.myproject.shuttleclub.club.infrastructure.persistence.repository.ClubMemberRepository;
import com.myproject.shuttleclub.common.error.ForbiddenException;
import com.myproject.shuttleclub.common.error.NotFoundException;
import com.myproject.shuttleclub.common.error.ErrorCodes;
import com.myproject.shuttleclub.session.application.dto.RsvpDto;
import com.myproject.shuttleclub.session.application.dto.SessionDto;
import com.myproject.shuttleclub.session.infrastructure.persistence.entity.*;
import com.myproject.shuttleclub.session.infrastructure.persistence.repository.SessionRepository;
import com.myproject.shuttleclub.session.infrastructure.persistence.repository.SessionRsvpRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionRsvpRepository rsvpRepository;
    private final ClubMemberRepository clubMemberRepository;

    public SessionService(SessionRepository sessionRepository,
                          SessionRsvpRepository rsvpRepository,
                          ClubMemberRepository clubMemberRepository) {
        this.sessionRepository = sessionRepository;
        this.rsvpRepository = rsvpRepository;
        this.clubMemberRepository = clubMemberRepository;
    }

    @Transactional
    public SessionDto createSession(UUID clubId, UUID currentUserId,
                                   String title, OffsetDateTime startTime,
                                   String location, String note) {
        requireActiveMember(clubId, currentUserId);
        // TODO (optional): require OWNER/ADMIN if bạn muốn chặt hơn

        // start_time rule B: now - 6 hours <= start_time
        OffsetDateTime min = OffsetDateTime.now().minusHours(6);
        if (startTime.isBefore(min)) {
            throw new ForbiddenException(ErrorCodes.SESSION_START_TIME_INVALID,
                "startTime must be >= now - 6 hours");
        }

        UUID id = UUID.randomUUID();
        SessionEntity entity = new SessionEntity(
            id, clubId, title, startTime, location, note, SessionStatus.SCHEDULED, currentUserId
        );

        SessionEntity saved = sessionRepository.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<SessionDto> listSessions(UUID clubId, UUID currentUserId,
    									SessionStatus status,
                                        OffsetDateTime from, OffsetDateTime to,
                                        Pageable pageable) {
    	requireActiveMember(clubId, currentUserId);

    	  boolean noRange = (from == null && to == null);

    	  if (noRange) {
    	    if (status == null) {
    	      return sessionRepository.findByClubId(clubId, pageable).map(this::toDto);
    	    }
    	    return sessionRepository.findByClubIdAndStatus(clubId, status, pageable).map(this::toDto);
    	  }

    	  // apply 90-day defaults (như Step 5)
    	  if (from != null && to == null) to = from.plusDays(90);
    	  else if (from == null) from = to.minusDays(90);

    	  if (from.isAfter(to)) {
    	    throw new ForbiddenException(ErrorCodes.INVALID_TIME_RANGE, "`from` must be <= `to`");
    	  }

    	  if (status == null) {
    	    return sessionRepository.findByClubIdAndStartTimeBetween(clubId, from, to, pageable).map(this::toDto);
    	  }
    	  return sessionRepository.findByClubIdAndStatusAndStartTimeBetween(clubId, status, from, to, pageable)
    	      .map(this::toDto);
    }

    @Transactional
    public RsvpDto updateMyRsvp(UUID sessionId, UUID currentUserId, RsvpStatus status) {
        SessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new NotFoundException(ErrorCodes.SESSION_NOT_FOUND, "Session not found"));

        requireActiveMember(session.getClubId(), currentUserId);

        if (session.getStatus() == SessionStatus.CLOSED) {
            throw new ForbiddenException(ErrorCodes.SESSION_CLOSED, "Session is closed; RSVP updates are not allowed");
        }

        OffsetDateTime now = OffsetDateTime.now();

        SessionRsvpEntity rsvp = rsvpRepository.findBySessionIdAndUserId(sessionId, currentUserId)
            .orElseGet(() -> new SessionRsvpEntity(UUID.randomUUID(), sessionId, currentUserId, status, now));

        // if existing, update
        rsvp.update(status, now);

        SessionRsvpEntity saved = rsvpRepository.save(rsvp);
        return new RsvpDto(saved.getId(), saved.getSessionId(), saved.getUserId(), saved.getStatus(), saved.getRespondedAt());
    }
    
    @Transactional
    public SessionDto closeSession(UUID sessionId, UUID currentUserId) {
      SessionEntity session = sessionRepository.findById(sessionId)
          .orElseThrow(() -> new NotFoundException(ErrorCodes.SESSION_NOT_FOUND, "Session not found"));

      requireAdminOrOwner(session.getClubId(), currentUserId);

      if (session.getStatus() == SessionStatus.CLOSED) {
        // idempotent: đã đóng rồi thì trả lại luôn
        return toDto(session);
      }

      session.close();
      // session is managed entity → flush at commit
      return toDto(session);
    }

    private void requireActiveMember(UUID clubId, UUID userId) {
    	  boolean ok = clubMemberRepository.existsMember(clubId, userId, MembershipStatus.ACTIVE);
    	  if (!ok) {
    	    throw new ForbiddenException(ErrorCodes.CLUB_MEMBERSHIP_REQUIRED, "User must be an active club member");
    	  }
    }

    private SessionDto toDto(SessionEntity e) {
        return new SessionDto(
            e.getId(), e.getClubId(), e.getTitle(), e.getStartTime(),
            e.getLocation(), e.getNote(), e.getStatus(), e.getCreatedBy()
        );
    }
    
    private void requireAdminOrOwner(UUID clubId, UUID userId) {
    	  boolean ok = clubMemberRepository.existsMemberWithRoles(
    		      clubId,
    		      userId,
    		      MembershipStatus.ACTIVE,
    		      java.util.List.of(ClubRole.OWNER, ClubRole.ADMIN)
    		  );

    		  if (!ok) {
    		    throw new ForbiddenException(ErrorCodes.CLUB_ADMIN_REQUIRED, "Only OWNER/ADMIN can perform this action");
    		  }
    	
    }
}
