package com.ahmadda.presentation.cookie;

import com.ahmadda.presentation.cookie.config.CookieProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieProvider {

    public ResponseCookie createCookie(final String name,
                                       final String value,
                                       final CookieProperties cookieProperties,
                                       final Duration ttl) {
        return ResponseCookie.from(name, value)
                .maxAge(ttl)
                .domain(cookieProperties.getDomain())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }

    public ResponseCookie expiresCookie(final String name, final CookieProperties cookieProperties) {
        return ResponseCookie.from(name, "")
                .maxAge(0)
                .domain(cookieProperties.getDomain())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }
}
