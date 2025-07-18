package com.ahmadda.presentation;

import com.ahmadda.application.AuthTokens;
import com.ahmadda.application.LoginService;
import com.ahmadda.presentation.dto.AccessTokenResponse;
import com.ahmadda.presentation.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    public static final int COOKIE_AGE_SECONDS = 604800;

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            final @RequestBody LoginRequest loginRequest,
            final HttpServletResponse response
    ) {
        AuthTokens authTokens = loginService.login(loginRequest.code());

        ResponseCookie cookie = ResponseCookie.from("refresh-token", authTokens.refreshToken())
                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .path("/")
                .maxAge(COOKIE_AGE_SECONDS)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AccessTokenResponse(authTokens.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> extendsToken(
            final @CookieValue(name = "refresh-token") String refreshToken
    ) {
        String accessToken = loginService.renewAuthTokens(refreshToken);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(accessTokenResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        ResponseCookie logoutCookie = ResponseCookie.from("refresh-token", "")
                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, logoutCookie.toString());

        return ResponseEntity.noContent().build();
    }
}
