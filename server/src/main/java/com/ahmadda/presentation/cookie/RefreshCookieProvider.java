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
public class RefreshCookieProvider {

    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final RefreshTokenCookieProperties refreshTokenCookieProperties;
    private final CookieProvider cookieProvider;

    public ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return cookieProvider.createCookie(REFRESH_TOKEN_KEY,
                                           refreshToken,
                                           refreshTokenCookieProperties,
                                           refreshTokenCookieProperties.getTtl()
        );
    }

    public ResponseCookie createLogoutRefreshTokenCookie() {
        return cookieProvider.expiresCookie(REFRESH_TOKEN_KEY, refreshTokenCookieProperties);
    }
}
