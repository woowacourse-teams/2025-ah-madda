package com.ahmadda.presentation.resolver;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.infra.login.jwt.JwtProvider;
import com.ahmadda.infra.login.jwt.dto.JwtMemberPayload;
import com.ahmadda.presentation.exception.InvalidAuthorizationException;
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
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER_TYPE = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMember.class) &&
                parameter.getParameterType()
                        .equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = extractAccessToken(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));

        JwtMemberPayload jwtMemberPayload = jwtProvider.parsePayload(accessToken);

        return new LoginMember(jwtMemberPayload.getMemberId());
    }

    private String extractAccessToken(final String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length())
                    .trim();
        }
        throw new InvalidAuthorizationException("인증 토큰 정보가 존재하지 않거나 유효하지 않습니다.");
    }
}
