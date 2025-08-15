package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.presentation.cookie.CookieProvider;
import com.ahmadda.presentation.dto.AccessTokenResponse;
import com.ahmadda.presentation.dto.LoginRequest;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "로그인 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class LoginController {

    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String USER_AGENT_KEY = "User-Agent";

    private final LoginService loginService;
    private final CookieProvider cookieProvider;

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
    public ResponseEntity<AccessTokenResponse> login(
            @RequestHeader(USER_AGENT_KEY) final String userAgent,
            @RequestBody final LoginRequest loginRequest) {
        MemberToken authTokens = loginService.login(loginRequest.code(), loginRequest.redirectUri(), userAgent);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(authTokens.accessToken());

        ResponseCookie refreshTokenCookie = cookieProvider.createRefreshTokenCookie(
                REFRESH_TOKEN_KEY,
                authTokens.refreshToken()
        );

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendToken(
            @RequestHeader(USER_AGENT_KEY) final String userAgent,
            @CookieValue(REFRESH_TOKEN_KEY) final String refreshToken,
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String headerAccessToken) {
        String accessToken = cookieProvider.extractBearer(headerAccessToken);

        MemberToken memberToken = loginService.renewMemberToken(accessToken, refreshToken, userAgent);
        ResponseCookie refreshTokenCookie =
                cookieProvider.createRefreshTokenCookie(REFRESH_TOKEN_KEY, memberToken.refreshToken());
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(memberToken.accessToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthMember final LoginMember loginMember,
            @RequestHeader(USER_AGENT_KEY) final String userAgent,
            @CookieValue(REFRESH_TOKEN_KEY) final String authRefreshToken) {
        String refreshToken = cookieProvider.extractBearer(authRefreshToken);
        loginService.logout(loginMember, refreshToken, userAgent);
        ResponseCookie logoutRefreshCookie = cookieProvider.createLogoutRefreshCookie(REFRESH_TOKEN_KEY);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, logoutRefreshCookie.toString())
                .build();
    }
}
