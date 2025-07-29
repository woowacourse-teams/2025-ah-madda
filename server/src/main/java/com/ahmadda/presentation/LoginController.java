package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import com.ahmadda.presentation.dto.AccessTokenResponse;
import com.ahmadda.presentation.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "로그인 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "구글 OAuth 로그인", description = "Google OAuth 인가 코드로 로그인을 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = AccessTokenResponse.class
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
                                              "detail": "유효하지 않은 인증 정보 입니다. 인가 코드가 만료되었거나, 잘못되었습니다.",
                                              "instance": "/api/members/login"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody final LoginRequest loginRequest) {
        String authTokens = loginService.login(loginRequest.code(), loginRequest.redirectUri());

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(authTokens);

        return ResponseEntity.ok(accessTokenResponse);
    }
}
