package com.ahmadda.presentation.cookie;

import com.ahmadda.presentation.cookie.config.RefreshTokenCookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@EnableConfigurationProperties(RefreshTokenCookieProperties.class)
@RequiredArgsConstructor
@Component
public class CookieProvider {

    private final static String LOGOUT_COOKIE_VALUE = "";
    private final static Duration LOGOUT_COOKIE_DURATION = Duration.ofSeconds(0);

    private final RefreshTokenCookieProperties refreshTokenCookieProperties;

    public ResponseCookie createRefreshTokenCookie(final String tokenKey, final String refreshToken) {
        return ResponseCookie.from(tokenKey, refreshToken)
                .maxAge(refreshTokenCookieProperties.getTtl())
                .sameSite(refreshTokenCookieProperties.getSameSite())
                .secure(refreshTokenCookieProperties.isSecure())
                .httpOnly(refreshTokenCookieProperties.isHttpOnly())
                .path(refreshTokenCookieProperties.getPath())
                .build();
    }

    public ResponseCookie createLogoutRefreshCookie(final String tokenKey) {
        return ResponseCookie.from(tokenKey, LOGOUT_COOKIE_VALUE)
                .maxAge(LOGOUT_COOKIE_DURATION)
                .sameSite(refreshTokenCookieProperties.getSameSite())
                .secure(refreshTokenCookieProperties.isSecure())
                .httpOnly(refreshTokenCookieProperties.isHttpOnly())
                .path(refreshTokenCookieProperties.getPath())
                .build();
    }
}
