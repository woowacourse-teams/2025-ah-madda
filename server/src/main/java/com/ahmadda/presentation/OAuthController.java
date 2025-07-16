package com.ahmadda.presentation;

import com.ahmadda.application.AuthService;
import com.ahmadda.application.CookieUtils;
import com.ahmadda.application.GoogleOAuthUserInfo;
import com.ahmadda.application.GoogleOAuthService;
import com.ahmadda.application.MemberService;
import com.ahmadda.domain.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController {

    private final GoogleOAuthService googleOAuthService;
    private final MemberService memberService;
    private final AuthService authService;

    @GetMapping("/login")
    public String redirectToOauthProvider(HttpServletResponse response) {
        String state = CookieUtils.generateState();
        String redirectUrl = googleOAuthService.generateGoogleAuthUrl(state);
        Cookie stateCookie = CookieUtils.createStateCookie(state);
        response.addCookie(stateCookie);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/callback/google")
    public String googleCallback(
            @RequestParam String code,
            @RequestParam String error,
            @RequestParam String state,
            @CookieValue(name = "oauth_state") String storedState,
            HttpServletResponse response) {
        if (error != null || storedState == null || !storedState.equals(state)) {
            return "error"; //todo: 예외 시 처리
        }

        Cookie deletedCookie = CookieUtils.deleteCookie("oauth_state");
        response.addCookie(deletedCookie);

        GoogleOAuthUserInfo googleOAuthUserInfo = googleOAuthService.authenticateGoogleUser(code);
        Member loginMember = memberService.processGoogleOAuthLogin(googleOAuthUserInfo);
        String loginToken = authService.publishLoginToken(loginMember);

        Cookie loginCookie = CookieUtils.createCookie("token", loginToken);
        response.addCookie(loginCookie);

        return "success"; //todo: 성공 시 처리
    }
}
