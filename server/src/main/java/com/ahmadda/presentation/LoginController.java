package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.presentation.cookie.CookieProvider;
import com.ahmadda.presentation.dto.AccessTokenResponse;
import com.ahmadda.presentation.dto.LoginRequest;
import com.ahmadda.presentation.header.HeaderProvider;
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

    private final LoginService loginService;
    private final CookieProvider cookieProvider;
    private final HeaderProvider headerProvider;

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
                                              "detail": "유효하지 않는 인증 정보입니다. 인가 코드가 만료되었거나, 잘못되었습니다.",
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

        ResponseCookie refreshTokenCookie = cookieProvider.createRefreshTokenCookie(
                memberToken.refreshToken()
        );

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @Operation(
            summary = "액세스 토큰 재발급",
            description = "만료된 액세스 토큰과 쿠키의 리프레시 토큰으로 새 토큰을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = AccessTokenResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "아직 만료되지 않는 액세스 토큰",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "엑세스 토큰이 만료되지 않았습니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """),
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
                                    name = "리프레시 토큰 불일치(저장값과 다름)",
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
                                    name = "JWT 형식 오류",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "잘못된 형식의 토큰입니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "JWT 서명/파싱 오류",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "인증 토큰을 파싱하는데 실패하였습니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """)
                    })
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "저장된 토큰 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "토큰을 찾을 수 없습니다.",
                                              "instance": "/api/members/token"
                                            }
                                            """)
                    })
            )
    })
    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendToken(
            @RequestHeader(HttpHeaders.USER_AGENT) final String userAgent,
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String headerAccessToken,
            @CookieValue(CookieProvider.REFRESH_TOKEN_KEY) final String refreshToken
    ) {
        String accessToken = headerProvider.extractAccessToken(headerAccessToken);

        MemberToken memberToken = loginService.renewMemberToken(accessToken, refreshToken, userAgent);
        ResponseCookie refreshTokenCookie =
                cookieProvider.createRefreshTokenCookie(memberToken.refreshToken());
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(memberToken.accessToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @Operation(
            summary = "로그아웃",
            description = "리프레시 토큰을 무효화하고 쿠키에서 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "멤버-토큰 불일치",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "토큰 정보가 일치하지 않습니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "JWT 형식 오류",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "잘못된 형식의 토큰입니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """),
                            @ExampleObject(
                                    name = "JWT 파싱 실패",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "인증 토큰을 파싱하는데 실패하였습니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """)
                    })
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "저장된 토큰 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "토큰을 찾을 수 없습니다.",
                                              "instance": "/api/members/logout"
                                            }
                                            """)
                    })
            )
    })
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthMember final LoginMember loginMember,
            @RequestHeader(HttpHeaders.USER_AGENT) final String userAgent,
            @CookieValue(CookieProvider.REFRESH_TOKEN_KEY) final String refreshToken
    ) {
        loginService.logout(loginMember, refreshToken, userAgent);

        ResponseCookie logoutRefreshTokenCookie = cookieProvider.createLogoutRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, logoutRefreshTokenCookie.toString())
                .build();
    }
}
