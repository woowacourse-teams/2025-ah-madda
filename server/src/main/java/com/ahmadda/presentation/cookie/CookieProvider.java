package com.ahmadda.presentation.cookie;

import com.ahmadda.presentation.cookie.config.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@EnableConfigurationProperties(CookieProperties.class)
@RequiredArgsConstructor
@Component
public class CookieProvider {

    private final static String LOGOUT_COOKIE_VALUE = "";
    private final static Duration LOGOUT_COOKIE_DURATION = Duration.ofSeconds(0);

    private final CookieProperties cookieProperties;

    public ResponseCookie createRefreshTokenCookie(final String tokenKey, final String refreshToken) {
        return ResponseCookie.from(tokenKey, refreshToken)
                .maxAge(cookieProperties.getTtl())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }

    public ResponseCookie createLogoutRefreshCookie(final String tokenKey) {
        return ResponseCookie.from(tokenKey, LOGOUT_COOKIE_VALUE)
                .maxAge(LOGOUT_COOKIE_DURATION)
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }
}
