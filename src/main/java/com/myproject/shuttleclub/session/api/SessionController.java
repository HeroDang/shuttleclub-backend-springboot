package com.myproject.shuttleclub.session.api;

import com.myproject.shuttleclub.common.api.ApiResponse;
import com.myproject.shuttleclub.common.api.PageInfo;
import com.myproject.shuttleclub.common.api.PagedResponse;
import com.myproject.shuttleclub.common.auth.CurrentUserProvider;
import com.myproject.shuttleclub.session.api.dto.CreateSessionRequest;
import com.myproject.shuttleclub.session.api.dto.RsvpResponse;
import com.myproject.shuttleclub.session.api.dto.SessionResponse;
import com.myproject.shuttleclub.session.api.dto.UpdateMyRsvpRequest;
import com.myproject.shuttleclub.session.application.SessionService;
import com.myproject.shuttleclub.session.application.dto.RsvpDto;
import com.myproject.shuttleclub.session.application.dto.SessionDto;
import com.myproject.shuttleclub.session.infrastructure.persistence.entity.SessionStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.myproject.shuttleclub.common.web.RequestIdSupport.requestId;

@RestController
@RequestMapping
public class SessionController {

  private final SessionService sessionService;
  private final CurrentUserProvider currentUserProvider;

  public SessionController(SessionService sessionService,
                           CurrentUserProvider currentUserProvider) {
    this.sessionService = sessionService;
    this.currentUserProvider = currentUserProvider;
  }

  // =========================
  // Create session
  // POST /clubs/{clubId}/sessions
  // =========================
  @PostMapping("/clubs/{clubId}/sessions")
  public ApiResponse<SessionResponse> createSession(
      @PathVariable UUID clubId,
      @Valid @RequestBody CreateSessionRequest request,
      HttpServletRequest httpRequest
  ) {
    UUID me = currentUserProvider.currentUserId();

    SessionDto dto = sessionService.createSession(
        clubId,
        me,
        request.title(),
        request.startTime(),
        request.location(),
        request.note()
    );

    return ApiResponse.ok(toResponse(dto), requestId(httpRequest));
  }

  // =========================
  // List sessions
  // GET /clubs/{clubId}/sessions?from=&to=&page=&size=
  // =========================
  @GetMapping("/clubs/{clubId}/sessions")
  public ApiResponse<PagedResponse<SessionResponse>> listSessions(
      @PathVariable UUID clubId,
      @RequestParam(required = false) OffsetDateTime from,
      @RequestParam(required = false) OffsetDateTime to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) SessionStatus status,
      HttpServletRequest httpRequest
  ) {
	  UUID me = currentUserProvider.currentUserId();

	  int safeSize = Math.min(Math.max(size, 1), 100); // cap 1..100
	  Pageable pageable = PageRequest.of(page, safeSize, Sort.by(
			    Sort.Order.desc("startTime"),
			    Sort.Order.desc("id")
			));

	  Page<SessionResponse> p = sessionService
			    .listSessions(clubId, me, status, from, to, pageable)
			    .map(SessionController::toResponse);

	  PagedResponse<SessionResponse> data = new PagedResponse<>(
	      p.getContent(),
	      new PageInfo(
	          p.getNumber(),
	          p.getSize(),
	          p.getTotalElements(),
	          p.getTotalPages(),
	          p.hasNext(),
	          p.hasPrevious()
	      )
	  );

	  return ApiResponse.ok(data, requestId(httpRequest));
  }

  // =========================
  // Update my RSVP
  // PUT /sessions/{sessionId}/rsvps/me
  // =========================
  @PutMapping("/sessions/{sessionId}/rsvps/me")
  public ApiResponse<RsvpResponse> updateMyRsvp(
      @PathVariable UUID sessionId,
      @Valid @RequestBody UpdateMyRsvpRequest request,
      HttpServletRequest httpRequest
  ) {
    UUID me = currentUserProvider.currentUserId();

    RsvpDto dto = sessionService.updateMyRsvp(
        sessionId,
        me,
        request.status()
    );

    return ApiResponse.ok(toResponse(dto), requestId(httpRequest));
  }
  
  @PostMapping("/sessions/{sessionId}/close")
  public ApiResponse<SessionResponse> closeSession(
      @PathVariable UUID sessionId,
      HttpServletRequest httpRequest
  ) {
    UUID me = currentUserProvider.currentUserId();
    SessionDto dto = sessionService.closeSession(sessionId, me);
    return ApiResponse.ok(toResponse(dto), requestId(httpRequest));
  }

  // =========================
  // Mappers
  // =========================
  private static SessionResponse toResponse(SessionDto dto) {
    return new SessionResponse(
        dto.id(),
        dto.clubId(),
        dto.title(),
        dto.startTime(),
        dto.location(),
        dto.note(),
        dto.status(),
        dto.createdBy()
    );
  }

  private static RsvpResponse toResponse(RsvpDto dto) {
    return new RsvpResponse(
        dto.id(),
        dto.sessionId(),
        dto.userId(),
        dto.status(),
        dto.respondedAt()
    );
  }
}
