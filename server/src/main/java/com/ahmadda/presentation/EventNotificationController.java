package com.ahmadda.presentation;

import com.ahmadda.application.EventNotificationService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.NonGuestsNotificationRequest;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                                              "title": "Forbidden",
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
            @PathVariable Long eventId,
            @RequestBody @Valid SelectedOrganizationMembersNotificationRequest request,
            @AuthMember LoginMember loginMember
    ) {
        eventNotificationService.notifySelectedOrganizationMembers(eventId, request, loginMember);

        return ResponseEntity.ok()
                .build();
    }
}
