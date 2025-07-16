package com.ahmadda.application;

import jakarta.servlet.http.Cookie;
import java.util.UUID;

public class CookieUtils {

    public static String generateState() {
        return UUID.randomUUID().toString();
    }

    public static Cookie createStateCookie(String state) {
        Cookie cookie = new Cookie("oauth_state", state);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(300);
        return cookie;
    }

    public static Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
