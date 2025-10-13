package com.ahmadda.presentation;

import com.ahmadda.application.EventGuestService;
import com.ahmadda.application.EventNotificationOptOutService;
import com.ahmadda.application.EventService;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.event.Answer;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestWithOptStatus;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberWithOptStatus;
import com.ahmadda.presentation.dto.EventDetailResponse;
import com.ahmadda.presentation.dto.GuestAnswerResponse;
import com.ahmadda.presentation.dto.GuestStatusResponse;
import com.ahmadda.presentation.dto.GuestWithOptOutResponse;
import com.ahmadda.presentation.dto.OrganizationMemberWithOptOutResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Event Guest", description = "이벤트 게스트 관련 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventGuestController {

    private final EventGuestService eventGuestService;
    private final EventService eventService;
    private final EventNotificationOptOutService eventNotificationOptOutService;

    @Operation(summary = "이벤트 게스트 목록 및 알림 수신 거부 여부 조회", description = "해당 이벤트에 참여한 게스트 목록과, 각 게스트의 알림 수신 거부 여부를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = GuestWithOptOutResponse.class))
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/guests"
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
                                              "detail": "존재하지 않는 이벤트입니다.",
                                              "instance": "/api/events/{eventId}/guests"
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
                                              "detail": "이벤트 스페이스의 구성원만 접근할 수 있습니다.",
                                              "instance": "/api/events/{eventId}/guests"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{eventId}/guests")
    public ResponseEntity<List<GuestWithOptOutResponse>> getGuests(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Guest> guestMembers = eventGuestService.getGuests(eventId, loginMember);
        List<GuestWithOptStatus> guestsWithOptOuts = eventNotificationOptOutService.mapGuests(guestMembers);

        List<GuestWithOptOutResponse> responses = guestsWithOptOuts.stream()
                .map(GuestWithOptOutResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "이벤트 미참여 구성원 목록 및 알림 수신 거부 여부 조회", description = "해당 이벤트에 아직 참여하지 않는 구성원 목록과, 각 구성원의 알림 수신 거부 여부를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationMemberWithOptOutResponse.class))
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/non-guests"
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
                                              "detail": "존재하지 않는 이벤트입니다.",
                                              "instance": "/api/events/{eventId}/non-guests"
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
                                              "detail": "이벤트 스페이스의 구성원만 접근할 수 있습니다.",
                                              "instance": "/api/events/{eventId}/non-guests"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{eventId}/non-guests")
    public ResponseEntity<List<OrganizationMemberWithOptOutResponse>> getNonGuests(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        List<OrganizationMember> nonGuestMembers =
                eventGuestService.getNonGuestOrganizationMembers(eventId, loginMember);
        List<OrganizationMemberWithOptStatus> nonGuestsWithOptOuts =
                eventNotificationOptOutService.mapOrganizationMembers(eventId, nonGuestMembers);

        List<OrganizationMemberWithOptOutResponse> responses = nonGuestsWithOptOuts.stream()
                .map(OrganizationMemberWithOptOutResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "그룹 내 비게스트 목록 및 알림 수신 거부 여부 조회", description = "이벤트 내 특정 그룹에 속하면서 아직 초대받지 않은(비게스트) 구성원 목록과, 각 구성원의 알림 수신 거부 여부를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationMemberWithOptOutResponse.class))
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/groups/{groupId}/non-guests"
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
                                              "detail": "이벤트 스페이스의 구성원만 접근할 수 있습니다.",
                                              "instance": "/api/events/{eventId}/groups/{groupId}/non-guests"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/groups/{groupId}/non-guests"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "그룹 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 그룹입니다.",
                                                      "instance": "/api/events/{eventId}/groups/{groupId}/non-guests"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/{eventId}/groups/{groupId}/non-guests")
    public ResponseEntity<List<OrganizationMemberWithOptOutResponse>> getGroupNonGuests(
            @PathVariable final Long eventId,
            @PathVariable final Long groupId,
            @AuthMember final LoginMember loginMember
    ) {
        List<OrganizationMember> nonGuestMembers =
                eventGuestService.getGroupNonGuestOrganizationMembers(eventId, groupId, loginMember);
        List<OrganizationMemberWithOptStatus> nonGuestsWithOptOuts =
                eventNotificationOptOutService.mapOrganizationMembers(eventId, nonGuestMembers);

        List<OrganizationMemberWithOptOutResponse> responses = nonGuestsWithOptOuts.stream()
                .map(OrganizationMemberWithOptOutResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "이벤트 참여", description = "이벤트 ID에 해당하는 이벤트에 참여합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = EventDetailResponse.class)
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/participation"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "질문 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 질문입니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 멤버가 이벤트를 생성한 이벤트 스페이스에 속해있지 않음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "같은 이벤트 스페이스의 이벤트에만 게스트로 참여할 수 있습니다합니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 신청 기간이 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이벤트 신청은 신청 시작 시간부터 신청 마감 시간까지 가능합니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "수용 인원이 가득참",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "수용 인원이 가득차 이벤트에 참여할 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 참여 중인 이벤트",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이미 해당 이벤트에 참여 중인 게스트입니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 주최자가 게스트로 참여할 경우",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이벤트의 주최자는 게스트로 참여할 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "필수 질문에 대한 답변 누락",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "필수 질문에 대한 답변이 누락되었습니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트에 포함되지 않는 질문에 대한 답변",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이벤트에 포함되지 않는 질문입니다.",
                                                      "instance": "/api/events/{eventId}/participation"
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @PostMapping("/{eventId}/participation")
    public ResponseEntity<EventDetailResponse> participateEvent(
            @PathVariable final Long eventId,
            @RequestBody @Valid final EventParticipateRequest eventParticipateRequest,
            @AuthMember final LoginMember loginMember
    ) {
        eventGuestService.participantEvent(
                eventId,
                loginMember,
                LocalDateTime.now(),
                eventParticipateRequest
        );

        Event event = eventService.getOrganizationMemberEvent(loginMember, eventId);
        return ResponseEntity.ok(EventDetailResponse.from(event));
    }

    @Operation(summary = "이벤트 참여 여부", description = "이벤트 ID에 해당하는 이벤트에 참여 여부를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = GuestStatusResponse.class
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/guest-status"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/guest-status"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/guest-status"
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @GetMapping("/{eventId}/guest-status")
    public ResponseEntity<GuestStatusResponse> isGuest(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        boolean isGuest = eventGuestService.isGuest(eventId, loginMember);

        return ResponseEntity.ok(new GuestStatusResponse(isGuest));
    }

    @Operation(summary = "이벤트 참여 취소", description = "이벤트 ID에 해당하는 이벤트 참여를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/cancel-participate"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/cancel-participate"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/cancel-participate"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "참여하지 않는 이벤트",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "이벤트의 참가자 목록에서 일치하는 구성원을 찾을 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/cancel-participate"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping("/{eventId}/cancel-participate")
    public ResponseEntity<Void> cancelParticipate(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        eventGuestService.cancelParticipation(eventId, loginMember);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "게스트 답변 조회", description = "게스트의 답변을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = GuestAnswerResponse.class))
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/guests/{guestId}/answers"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/answers"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/answers"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "게스트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 게스트입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/answers"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "답변을 볼 권한이 없습니다.",
                                              "instance": "/api/events/{eventId}/guests/{guestId}/answers"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{eventId}/guests/{guestId}/answers")
    public ResponseEntity<List<GuestAnswerResponse>> getAnswers(
            @PathVariable final Long eventId,
            @PathVariable final Long guestId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Answer> answers = eventGuestService.getAnswers(eventId, guestId, loginMember);

        List<GuestAnswerResponse> guestAnswerResponses = answers.stream()
                .map(GuestAnswerResponse::from)
                .toList();

        return ResponseEntity.ok(guestAnswerResponses);
    }

    @Operation(summary = "게스트 승인", description = "게스트의 상태를 승인 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "게스트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 게스트입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "주최자가 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "주최자만 게스트를 승인할 수 있습니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "승인 이벤트가 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "승인 가능한 이벤트가 아니라 승인 상태를 변경할 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "수용 인원이 가득참",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "수용 인원이 가득차 해당 게스트를 승인할 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/approve"
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @PostMapping("/{eventId}/guests/{guestId}/approve")
    public ResponseEntity<Void> receiveApprovalFromOrganizer(
            @PathVariable final Long eventId,
            @PathVariable final Long guestId,
            @AuthMember final LoginMember loginMember
    ) {
        eventGuestService.receiveApprovalFromOrganizer(eventId, guestId, loginMember);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "게스트 승인 거절", description = "게스트의 상태를 거절 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/guests/{guestId}/reject"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/reject"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/reject"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "게스트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 게스트입니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/reject"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "주최자가 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "주최자만 게스트를 승인할 수 있습니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/reject"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "승인 이벤트가 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "승인 가능한 이벤트가 아니라 승인 상태를 변경할 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/guests/{guestId}/reject"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/{eventId}/guests/{guestId}/reject")
    public ResponseEntity<Void> receiveRejectFromOrganizer(
            @PathVariable final Long eventId,
            @PathVariable final Long guestId,
            @AuthMember final LoginMember loginMember
    ) {
        eventGuestService.receiveRejectFromOrganizer(eventId, guestId, loginMember);

        return ResponseEntity.ok()
                .build();
    }
}
