package com.ahmadda.presentation.cookie;

import com.ahmadda.presentation.cookie.config.RefreshTokenCookieProperties;
import com.ahmadda.presentation.exception.InvalidAuthorizationException;
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

    private static final String BEARER_TYPE = "Bearer ";

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

    public String resolveRefreshToken(final String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length())
                    .trim();
        }

        throw new InvalidAuthorizationException("인증 토큰 정보가 존재하지 않거나 유효하지 않습니다.");
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
