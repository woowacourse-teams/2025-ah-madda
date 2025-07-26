package com.ahmadda.presentation;

import com.ahmadda.application.LoginService;
import com.ahmadda.presentation.dto.AccessTokenResponse;
import com.ahmadda.presentation.dto.LoginRequest;
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

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody final LoginRequest loginRequest) {
        String authTokens = loginService.login(loginRequest.code());

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(authTokens);

        return ResponseEntity.ok(accessTokenResponse);
    }
}
