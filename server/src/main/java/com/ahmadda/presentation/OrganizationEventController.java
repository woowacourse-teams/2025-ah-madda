package com.ahmadda.presentation;


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
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Organization Event", description = "조직 이벤트 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationEventController {

    private final OrganizationService organizationService;
    private final OrganizationMemberEventService organizationMemberEventService;
    private final EventService eventService;

    @Operation(summary = "조직의 모든 이벤트 조회", description = "특정 조직에 속한 모든 이벤트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))
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
                                              "detail": "조직에 참여하지 않아 권한이 없습니다.",
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
            )
    })
    @GetMapping("/{organizationId}/events")
    public ResponseEntity<List<EventResponse>> getOrganizationEvents(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Event> organizationEvents = organizationService.getOrganizationEvents(organizationId, loginMember);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @Operation(summary = "이벤트 생성", description = "조직 ID에 속한 이벤트를 생성합니다. 해당 조직 ID에 속한 조직원만 이벤트를 생성할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    headers = {
                            @Header(name = "Location", schema = @Schema(type = "string"))
                    },
                    content = @Content(
                            schema = @Schema(
                                    implementation = EventCreateResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            mediaType = "application/json",
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
                            mediaType = "application/json",
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
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "조직 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않은 조직 정보입니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 회원입니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 종료 시간이 시작 시간보다 과거",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "종료 시간은 시작 시간보다 미래여야 합니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 시작 시간이 현재보다 과거",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이벤트 시작 시간은 현재 시점보다 미래여야 합니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "신청 기간과 이벤트 기간 겹침",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "신청 기간과 이벤트 기간이 겹칠 수 없습니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "신청 기간이 이벤트 기간보다 늦음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "신청 기간은 이벤트 기간보다 앞서야 합니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "다른 조직에서 생성 시도",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "최대 인원 범위 초과",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "최대 수용 인원은 1명보다 적거나 21억명 보다 클 수 없습니다.",
                                                      "instance": "/api/organizations/{organizationId}/events"
                                                    }
                                                    """
                                    )
                            }
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

    @Operation(summary = "이벤트 모집 마감", description = "이벤트 모집 마감합니다. 주최자만 모집 마감을 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/events/{eventId}/close/registration"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "조직에 소속되지 않은 멤버입니다.",
                                              "instance": "/api/organizations/events/{eventId}/close/registration"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "회원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 회원입니다.",
                                                      "instance": "/api/organizations/events/{eventId}/close/registration"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "신청 기간과 이벤트 기간 겹침",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "신청 기간과 이벤트 기간이 겹칠 수 없습니다.",
                                                      "instance": "/api/organizations/events/{eventId}/close/registration"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "신청 기간이 이벤트 기간보다 늦음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "신청 기간은 이벤트 기간보다 앞서야 합니다.",
                                                      "instance": "/api/organizations/events/{eventId}/close/registration"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/{organizationId}/events/{eventId}/registration/close")
    public ResponseEntity<Void> closeOrganizationEvent(
            @PathVariable final Long organizationId,
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember) {
        eventService.closeEventRegistration(
                organizationId,
                eventId,
                loginMember.memberId(),
                LocalDateTime.now()
        );

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 ID에 해당하는 이벤트를 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = EventDetailResponse.class
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
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))
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
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))
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
