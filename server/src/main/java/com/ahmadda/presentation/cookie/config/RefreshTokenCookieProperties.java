package com.ahmadda.presentation.cookie.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cookie.refresh-token")
@Getter
public class RefreshTokenCookieProperties {

    private final String path;
    private final String sameSite;
    private final boolean secure;
    private final boolean httpOnly;
    private final Duration ttl;

    public RefreshTokenCookieProperties(String path, String sameSite, boolean secure, boolean httpOnly, Duration ttl) {
        validateProperties(path, sameSite, ttl);

        this.path = path;
        this.sameSite = sameSite;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.ttl = ttl;
    }

    private void validateProperties(final String path,
                                    final String sameSite,
                                    final Duration ttl) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("쿠키 Path가 비어있습니다.");
        }
        if (sameSite == null || sameSite.isBlank()) {
            throw new IllegalArgumentException("쿠키 SameSite가 비어있습니다.");
        }
        if (ttl == null) {
            throw new IllegalArgumentException("쿠키 만료 시간이 지정되지 않았습니다.");
        }
    }
}
