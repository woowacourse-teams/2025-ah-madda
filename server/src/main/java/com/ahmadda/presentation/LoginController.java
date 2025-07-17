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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    public static final int COOKIE_AGE_SECONDS = 604800;

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest,
                                                     HttpServletResponse response) {
        AuthTokens authTokens = loginService.login(loginRequest.code());

        ResponseCookie cookie = ResponseCookie.from("refresh-token", authTokens.refreshToken())
                .maxAge(COOKIE_AGE_SECONDS)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AccessTokenResponse(authTokens.accessToken()));
    }
}
