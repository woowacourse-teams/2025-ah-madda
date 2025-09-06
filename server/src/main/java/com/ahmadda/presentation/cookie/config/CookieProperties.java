package com.ahmadda.presentation.cookie.config;

import lombok.Getter;

@Getter
public class CookieProperties {

    private final String path;
    private final String domain;
    private final String sameSite;
    private final boolean secure;
    private final boolean httpOnly;

    public CookieProperties(String path, String domain, String sameSite, boolean secure, boolean httpOnly) {
        validateCookieProperties(path, domain, sameSite);

        this.path = path;
        this.domain = domain;
        this.sameSite = sameSite;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    private void validateCookieProperties(
            final String path,
            final String domain,
            final String sameSite
    ) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("쿠키 Path가 비어있습니다.");
        }

        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("쿠키 Domain이 비어있습니다.");
        }

        if (sameSite == null || sameSite.isBlank()) {
            throw new IllegalArgumentException("쿠키 SameSite가 비어있습니다.");
        }
    }
}
