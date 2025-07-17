package com.ahmadda.application;

import jakarta.servlet.http.Cookie;

import java.util.UUID;

public class CookieUtils {

    public static Cookie createCookie(final String name, final String value, final int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    public static Cookie createStateCookie(final String state) {
        Cookie cookie = new Cookie("oauth_state", state);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(300);

        return cookie;
    }

    public static Cookie deleteCookie(final String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return cookie;
    }

    public static String generateState() {
        return UUID.randomUUID().toString();
    }
}
