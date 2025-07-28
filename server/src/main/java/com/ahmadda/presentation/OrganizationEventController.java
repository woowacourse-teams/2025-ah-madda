package com.ahmadda.presentation;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmadda.application.EventService;
import com.ahmadda.application.OrganizationMemberEventService;
import com.ahmadda.application.OrganizationService;
import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.Event;
import com.ahmadda.presentation.dto.EventCreateResponse;
import com.ahmadda.presentation.dto.EventDetailResponse;
import com.ahmadda.presentation.dto.EventResponse;
import com.ahmadda.presentation.resolver.AuthMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Organization Event", description = "조직 이벤트 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationEventController {

    private final OrganizationService organizationService;
    private final OrganizationMemberEventService organizationMemberEventService;
    private final EventService eventService;

    @Operation(summary = "조직 이벤트 목록 조회", description = "특정 조직의 모든 이벤트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 조직입니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unprocessable Entity",
                                              "status": 422,
                                              "detail": "조직에 참여하지 않아 권한이 없습니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{organizationId}/events")
    public ResponseEntity<List<EventResponse>> getOrganizationEvents(@PathVariable final Long organizationId,
                                                                     @AuthMember final LoginMember loginMember) {
        List<Event> organizationEvents = organizationService.getOrganizationEvents(organizationId, loginMember);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @Operation(summary = "조직 이벤트 생성", description = "특정 조직에서 새로운 이벤트를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Bad Request",
                                              "status": 400,
                                              "detail": "제목은 공백이면 안됩니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "조직에 소속되지 않은 멤버입니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 조직입니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unprocessable Entity",
                                              "status": 422,
                                              "detail": "자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.",
                                              "instance": "/api/organizations/{organizationId}/events"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{organizationId}/events")
    public ResponseEntity<EventCreateResponse> createOrganizationEvent(
            @PathVariable final Long organizationId,
            @RequestBody @Valid final EventCreateRequest eventCreateRequest,
            @AuthMember final LoginMember loginMember
    ) {
        Event event = eventService.createEvent(
                organizationId,
                loginMember.memberId(),
                eventCreateRequest,
                LocalDateTime.now()
        );

        return ResponseEntity.created(URI.create("/api/organizations/" + organizationId + "/events/" + event.getId()))
                .body(new EventCreateResponse(event.getId()));
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 ID로 특정 이벤트의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않은 이벤트 정보입니다.",
                                              "instance": "/api/organizations/events/{eventId}"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDetailResponse> getOrganizationEvent(@PathVariable final Long eventId) {
        Event event = eventService.getEvent(eventId);

        return ResponseEntity.ok(EventDetailResponse.from(event));
    }

    @Operation(summary = "내가 주최한 이벤트 목록 조회", description = "로그인한 사용자가 주최한 이벤트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/{organizationId}/events/owned"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않은 조직원 정보입니다.",
                                              "instance": "/api/organizations/{organizationId}/events/owned"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{organizationId}/events/owned")
    public ResponseEntity<List<EventResponse>> getOwnerEvents(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Event> organizationEvents = organizationMemberEventService.getOwnerEvents(organizationId, loginMember);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @Operation(summary = "내가 참가한 이벤트 목록 조회", description = "로그인한 사용자가 참가한 이벤트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/{organizationId}/events/participated"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않은 조직원 정보입니다.",
                                              "instance": "/api/organizations/{organizationId}/events/participated"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{organizationId}/events/participated")
    public ResponseEntity<List<EventResponse>> getParticipantEvents(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Event> organizationEvents =
                organizationMemberEventService.getParticipantEvents(organizationId, loginMember);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
