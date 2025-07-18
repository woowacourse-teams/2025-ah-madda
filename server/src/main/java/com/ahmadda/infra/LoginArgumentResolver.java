package com.ahmadda.infra;

import com.ahmadda.application.JwtTokenProvider;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER_TYPE = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class) &&
                parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = extractAccessToken(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));

        long memberId = jwtTokenProvider.extractId(accessToken);

        return new LoginMember(memberId);
    }

    private String extractAccessToken(String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw new BusinessRuleViolatedException("accessToken이 옳바르지 않습니다.");
    }
}
