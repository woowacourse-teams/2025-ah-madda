package com.ahmadda.presentation;

import com.ahmadda.application.EventNotificationService;
import com.ahmadda.application.EventPokeService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.NonGuestsNotificationRequest;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.presentation.dto.NotifyPokeRequest;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Event Notification", description = "이벤트 알림 관련 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventNotificationController {

    private final EventNotificationService eventNotificationService;
    private final EventPokeService eventPokeService;

    @Operation(summary = "이벤트 미참여 조직원에게 알림 발송", description = "주최자가 이벤트에 참여하지 않은 조직원들에게 이메일 알림을 발송합니다.")
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
                                              "instance": "/api/events/{eventId}/notify-non-guests"
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
                                              "detail": "이벤트 주최자가 아닙니다.",
                                              "instance": "/api/events/{eventId}/notify-non-guests"
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
                                              "instance": "/api/events/{eventId}/notify-non-guests"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{eventId}/notify-non-guests")
    public ResponseEntity<Void> notifyNonGuests(
            @PathVariable final Long eventId,
            @RequestBody @Valid final NonGuestsNotificationRequest request,
            @AuthMember final LoginMember loginMember
    ) {
        eventNotificationService.notifyNonGuestOrganizationMembers(eventId, request, loginMember);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(
            summary = "선택된 조직원에게 알림 발송",
            description = "주최자가 선택한 조직원들에게 이메일 알림을 발송합니다."
    )
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
                                              "instance": "/api/events/{eventId}/notify-organization-members"
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
                                              "title": "`Forbidden`",
                                              "status": 403,
                                              "detail": "이벤트 주최자가 아닙니다.",
                                              "instance": "/api/events/{eventId}/notify-organization-members"
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
                                                      "instance": "/api/events/{eventId}/notify-organization-members"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "조직원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 조직원입니다.",
                                                      "instance": "/api/events/{eventId}/notify-organization-members"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    @PostMapping("/{eventId}/notify-organization-members")
    public ResponseEntity<Void> notifyOrganizationMembers(
            @PathVariable final Long eventId,
            @RequestBody @Valid final SelectedOrganizationMembersNotificationRequest request,
            @AuthMember final LoginMember loginMember
    ) {
        eventNotificationService.notifySelectedOrganizationMembers(eventId, request, loginMember);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "이벤트 참여자 푸시 알림", description = "참여자가 특정 참여자에게 푸시 알림을 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Bad Request",
                                              "status": 400,
                                              "detail": "잘못된 요청입니다.",
                                              "instance": "/api/events/{eventId}/notify-poke"
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/notify-poke"
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
                                              "detail": "이벤트에 대한 접근 권한이 없습니다.",
                                              "instance": "/api/events/{eventId}/notify-poke"
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
                                              "instance": "/api/events/{eventId}/notify-poke"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "포키 전송 제한",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "포키는 30분마다 한 대상에게 최대 10번만 보낼 수 있습니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "주최자 대상 제한",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "주최자에게 포키를 보낼 수 없습니다",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "자기 자신 대상 제한",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "스스로에게 포키를 보낼 수 없습니다",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "조직 참여 필수",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "포키를 보내려면 해당 조직에 참여하고 있어야 합니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "대상 조직 참여 필수",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "포키 대상이 해당 조직에 참여하고 있어야 합니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/{eventId}/notify-poke")
    public ResponseEntity<Void> notifyPoke(
            @PathVariable final Long eventId,
            @RequestBody @Valid final NotifyPokeRequest notifyPokeRequest,
            @AuthMember final LoginMember loginMember
    ) {
        eventPokeService.poke(eventId, notifyPokeRequest, loginMember);

        return ResponseEntity.ok()
                .build();
    }
}
