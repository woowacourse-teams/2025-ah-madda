package com.ahmadda.presentation;

import com.ahmadda.application.FcmRegistrationTokenService;
import com.ahmadda.application.dto.FcmRegistrationTokenRequest;
import com.ahmadda.application.dto.LoginMember;
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

@Tag(name = "FCM Registration Token", description = "FCM 등록 토큰 관련 API")
@RestController
@RequestMapping("/api/fcm-registration-tokens")
@RequiredArgsConstructor
public class FcmRegistrationTokenController {

    private final FcmRegistrationTokenService fcmRegistrationTokenService;

    @Operation(summary = "FCM 등록 토큰 저장", description = "로그인한 사용자의 디바이스로부터 전달된 FCM 등록 토큰을 서버에 저장합니다.")
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
                                              "detail": "유효하지 않는 인증 정보입니다.",
                                              "instance": "/api/fcm-registration-tokens"
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
                                              "instance": "/api/fcm-registration-tokens"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> registerFcmRegistrationToken(
            @RequestBody @Valid final FcmRegistrationTokenRequest request,
            @AuthMember final LoginMember loginMember
    ) {
        fcmRegistrationTokenService.registerFcmRegistrationToken(request, loginMember);
        return ResponseEntity.noContent()
                .build();
    }

    // TODO. 추후 DeleteMapping API 추가 고려 (예: 로그아웃시, JWT 인증 실패시)
}
