package com.ahmadda.presentation.cookie;

import com.ahmadda.presentation.cookie.config.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties(CookieProperties.class)
@RequiredArgsConstructor
@Component
public class CookieProvider {

    private final CookieProperties cookieProperties;

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh-token", refreshToken)
                .maxAge(cookieProperties.getTtl())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }
}
