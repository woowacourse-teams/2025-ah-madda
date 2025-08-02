package com.ahmadda.presentation;

import com.ahmadda.application.PushNotificationRecipientService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.PushNotificationRecipientRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Push Notification", description = "푸시 알림 수신자 관련 API")
@RestController
@RequestMapping("/api/push-notification")
@RequiredArgsConstructor
public class PushNotificationRecipientController {

    private final PushNotificationRecipientService pushNotificationRecipientService;

    @Operation(summary = "푸시 알림 수신자 등록", description = "로그인한 사용자의 디바이스를 푸시 알림 수신자로 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
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
                                              "instance": "/api/push-notification"
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
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/push-notification"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> registerRecipient(
            @RequestBody @Valid final PushNotificationRecipientRequest request,
            @AuthMember final LoginMember loginMember
    ) {
        pushNotificationRecipientService.registerRecipient(request, loginMember);

        return ResponseEntity.noContent()
                .build();
    }

    // TODO. 추후 DeleteMapping API 추가 고려 (예: 로그아웃시, JWT 인증 실패시)
}
