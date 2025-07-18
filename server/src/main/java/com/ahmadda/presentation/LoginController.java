package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<AccessTokenResponse> login(final @RequestBody LoginRequest loginRequest) {
        String authTokens = loginService.login(loginRequest.code());

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(authTokens);

        return ResponseEntity.ok().body(accessTokenResponse);
    }
}
