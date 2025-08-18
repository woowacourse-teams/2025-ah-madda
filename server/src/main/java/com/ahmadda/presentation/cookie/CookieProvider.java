package com.ahmadda.presentation.cookie;

import com.ahmadda.presentation.cookie.config.RefreshTokenCookieProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties(RefreshTokenCookieProperties.class)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
public class CookieProvider {

    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    
    private final RefreshTokenCookieProperties refreshTokenCookieProperties;

    public ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
                .maxAge(refreshTokenCookieProperties.getTtl())
                .sameSite(refreshTokenCookieProperties.getSameSite())
                .secure(refreshTokenCookieProperties.isSecure())
                .httpOnly(refreshTokenCookieProperties.isHttpOnly())
                .path(refreshTokenCookieProperties.getPath())
                .build();
    }


    public ResponseCookie createLogoutRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, "")
                .maxAge(0)
                .sameSite(refreshTokenCookieProperties.getSameSite())
                .secure(refreshTokenCookieProperties.isSecure())
                .httpOnly(refreshTokenCookieProperties.isHttpOnly())
                .path(refreshTokenCookieProperties.getPath())
                .build();
    }
}
