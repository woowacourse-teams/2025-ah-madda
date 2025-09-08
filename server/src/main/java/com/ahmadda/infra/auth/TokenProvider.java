package com.ahmadda.infra.auth;

import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProvider jwtProvider;

    public MemberToken createMemberToken(final Long memberId) {
        String accessToken = jwtProvider.createAccessToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        return new MemberToken(accessToken, refreshToken);
    }

    public LocalDateTime parseRefreshTokenExpiresAt(final String refreshToken) {
        JwtMemberPayload jwtMemberPayload = jwtProvider.parseRefreshPayload(refreshToken);

        return jwtMemberPayload.getExpiresAt();
    }

    public Long parseRefreshTokenMemberId(final String refreshToken) {
        JwtMemberPayload jwtMemberPayload = jwtProvider.parseRefreshPayload(refreshToken);

        return jwtMemberPayload.getMemberId();
    }

    public Optional<Boolean> checkRefreshTokenExpired(final String refreshToken) {
        return jwtProvider.isRefreshTokenExpired(refreshToken);
    }

    public Optional<Boolean> checkAccessTokenExpired(final String accessToken) {
        return jwtProvider.isAccessTokenExpired(accessToken);
    }
}
