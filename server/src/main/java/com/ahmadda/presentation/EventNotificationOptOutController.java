package com.ahmadda.presentation;

import com.ahmadda.application.EventNotificationOptOutService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.organization.OrganizationMemberWithOptStatus;
import com.ahmadda.presentation.dto.OptOutStatusResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Event Notification Opt-Out", description = "이벤트 알림 수신 거부 설정 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventNotificationOptOutController {

    private final EventNotificationOptOutService optOutService;

    @Operation(
            summary = "이벤트 알림 수신 거부 설정",
            description = "해당 이벤트에 대해 이메일 및 푸시 알림을 더 이상 받지 않도록 설정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "유효하지 않은 인증 정보입니다.",
                              "instance": "/api/events/{eventId}/notification/opt-out"
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 구성원",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/notification/opt-out"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 이벤트",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다",
                                                      "instance": "/api/events/{eventId}/notification/opt-out"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unprocessable Entity",
                              "status": 422,
                              "detail": "이미 해당 이벤트에 대한 알림 수신 거부가 설정되어 있습니다.",
                              "instance": "/api/events/{eventId}/notification/opt-out"
                            }
                            """))
            )
    })
    @PostMapping("/{eventId}/notification/opt-out")
    public ResponseEntity<Void> optOut(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        optOutService.optOut(eventId, loginMember);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(
            summary = "이벤트 알림 수신 거부 해제",
            description = "기존에 설정한 알림 수신 거부를 해제하여 다시 알림을 받을 수 있도록 설정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "유효하지 않은 인증 정보입니다.",
                              "instance": "/api/events/{eventId}/notification/opt-out"
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 구성원",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/notification/opt-out"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 이벤트",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다",
                                                      "instance": "/api/events/{eventId}/notification/opt-out"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unprocessable Entity",
                              "status": 422,
                              "detail": "존재하지 않는 수신 거부 설정입니다.",
                              "instance": "/api/events/{eventId}/notification/opt-out"
                            }
                            """))
            )
    })
    @DeleteMapping("/{eventId}/notification/opt-out")
    public ResponseEntity<Void> cancelOptOut(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        optOutService.cancelOptOut(eventId, loginMember);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(
            summary = "이벤트 알림 수신 거부 여부 조회",
            description = "현재 로그인한 사용자가 해당 이벤트에 대해 알림 수신 거부 상태인지 여부를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = OptOutStatusResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "유효하지 않은 인증 정보입니다.",
                              "instance": "/api/events/{eventId}/notification/opt-out"
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 구성원",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/events/{eventId}/notification/opt-out"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 이벤트",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다",
                                                      "instance": "/api/events/{eventId}/notification/opt-out"
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @GetMapping("/{eventId}/notification/opt-out")
    public ResponseEntity<OptOutStatusResponse> getOptOutStatus(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        OrganizationMemberWithOptStatus organizationMemberWithOptStatus =
                optOutService.getMemberWithOptStatus(eventId, loginMember);

        OptOutStatusResponse response = OptOutStatusResponse.from(organizationMemberWithOptStatus);

        return ResponseEntity.ok(response);
    }
}
