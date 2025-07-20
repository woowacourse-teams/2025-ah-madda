package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import com.ahmadda.presentation.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(final @RequestBody LoginRequest loginRequest) {
        String authTokens = loginService.login(loginRequest.code());

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(authTokens);

        return ResponseEntity.ok(accessTokenResponse);
    }
}
