package com.ahmadda.presentation;

import com.ahmadda.application.CookieUtils;
import com.ahmadda.application.OAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    private final OAuthService oAuthService;

    @GetMapping("/login")
    public String redirectToOauthProvider(HttpServletResponse response) {
        String state = CookieUtils.generateState();
        String redirectUrl = oAuthService.generateGoogleAuthUrl(state);
        Cookie stateCookie = CookieUtils.createStateCookie(state);
        response.addCookie(stateCookie);

        return "redirect:" + redirectUrl;
    }
}
