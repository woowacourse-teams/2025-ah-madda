package com.ahmadda.presentation.cookie.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cookie.refresh-token")
@Getter
public class RefreshTokenCookieProperties extends CookieProperties {

    private final Duration ttl;

    public RefreshTokenCookieProperties(
            final String path,
            final String domain,
            final String sameSite,
            final boolean secure,
            final boolean httpOnly,
            final Duration ttl
    ) {
        super(path, domain, sameSite, secure, httpOnly);

        validateRefreshProperties(ttl);

        this.ttl = ttl;
    }

    private void validateRefreshProperties(
            final Duration ttl
    ) {
        if (ttl == null) {
            throw new IllegalArgumentException("쿠키 만료 시간이 지정되지 않았습니다.");
        }
    }
}
