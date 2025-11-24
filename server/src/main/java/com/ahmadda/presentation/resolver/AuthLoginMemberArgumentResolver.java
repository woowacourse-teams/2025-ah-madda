package com.ahmadda.presentation.resolver;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.UnauthorizedException;
import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import com.ahmadda.presentation.header.HeaderProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtAccessTokenProperties.class)
public class AuthLoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtAccessTokenProperties jwtAccessTokenProperties;
    private final JwtProvider jwtProvider;
    private final HeaderProvider headerProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class) &&
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
        String accessToken = headerProvider.extractAccessToken(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));

        boolean isExpired = jwtProvider.isTokenExpired(accessToken, jwtAccessTokenProperties.getAccessSecretKey());
        if (isExpired) {
            throw new UnauthorizedException("만료기한이 지난 토큰입니다.");
        }

        JwtMemberPayload payload = jwtProvider.parsePayload(accessToken, jwtAccessTokenProperties.getAccessSecretKey());

        return new LoginMember(payload.memberId());
    }
}
