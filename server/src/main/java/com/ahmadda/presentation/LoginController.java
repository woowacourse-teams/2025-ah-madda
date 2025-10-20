package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.presentation.cookie.RefreshCookieProvider;
import com.ahmadda.presentation.dto.AccessTokenResponse;
import com.ahmadda.presentation.dto.LoginRequest;
import com.ahmadda.presentation.resolver.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
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

    private final LoginService loginService;
    private final RefreshCookieProvider refreshCookieProvider;

    @Operation(summary = "구글 OAuth 로그인", description = "구글 OAuth 인가 코드를 사용해 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    headers = {@Header(name = "Set-Cookie", description = "RefreshToken")},
                    content = @Content(
                            schema = @Schema(implementation = AccessTokenResponse.class)
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
                                              "instance": "/api/members/login"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @RequestHeader(HttpHeaders.USER_AGENT) final String userAgent,
            @RequestBody final LoginRequest loginRequest
    ) {
        MemberToken memberToken = loginService.login(loginRequest.code(), loginRequest.redirectUri(), userAgent);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(memberToken.accessToken());

        ResponseCookie refreshTokenCookie = refreshCookieProvider.createRefreshTokenCookie(memberToken.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @Operation(
            summary = "액세스 토큰 재발급",
            description = "쿠키의 리프레시 토큰으로 새 토큰을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "리프레시 토큰 만료",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "리프레시 토큰이 만료되었습니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "리프레시 토큰 불일치",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "리프레시 토큰이 유효하지 않습니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "잘못된 형식/서명 오류",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """)
                    })
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장된 토큰 없음",
                    content = @Content(examples = @ExampleObject(
                            value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Not Found",
                                      "status": 404,
                                      "detail": "존재하지 않는 리프레시 토큰입니다.",
                                      "instance": "/api/members/token"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendToken(
            @RequestHeader(HttpHeaders.USER_AGENT) final String userAgent,
            @CookieValue(RefreshCookieProvider.REFRESH_TOKEN_KEY) final String refreshToken
    ) {
        MemberToken memberToken = loginService.renewMemberToken(refreshToken, userAgent);
        ResponseCookie refreshTokenCookie = refreshCookieProvider.createRefreshTokenCookie(memberToken.refreshToken());
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(memberToken.accessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @Operation(
            summary = "로그아웃",
            description = "리프레시 토큰을 무효화하고 쿠키에서 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "리프레시 토큰 불일치",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "리프레시 토큰이 유효하지 않습니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "잘못된 형식/서명 오류",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """)
                    })
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원 또는 저장된 토큰 없음",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "존재하지 않는 회원",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "저장된 토큰 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 리프레시 토큰입니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """)
                    })
            )
    })
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @Auth final LoginMember loginMember,
            @RequestHeader(HttpHeaders.USER_AGENT) final String userAgent,
            @CookieValue(RefreshCookieProvider.REFRESH_TOKEN_KEY) final String refreshToken
    ) {
        loginService.logout(loginMember, refreshToken, userAgent);

        ResponseCookie logoutRefreshTokenCookie = refreshCookieProvider.createLogoutRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, logoutRefreshTokenCookie.toString())
                .build();
    }
}
