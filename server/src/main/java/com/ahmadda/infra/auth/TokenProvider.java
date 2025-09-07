package com.ahmadda.infra.auth;

import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.infra.auth.exception.InvalidTokenException;
import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProvider jwtProvider;
    private final HashEncoder hashEncoder;

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

    public MemberToken refreshMemberToken(final String accessToken,
                                          final String refreshToken,
                                          final String savedRefreshToken) {
        validateTokens(accessToken, refreshToken);

        Long memberId = parseRefreshTokenMemberId(refreshToken);

        validateRefreshTokenMatches(refreshToken, savedRefreshToken);

        return createMemberToken(memberId);
    }

    public void validateRefreshTokenMatch(final String refreshToken,
                                          final String savedRefreshToken,
                                          final Long memberId) {
        Long refreshTokenMemberId = parseRefreshTokenMemberId(refreshToken);
        if (!Objects.equals(memberId, refreshTokenMemberId)) {
            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
        }

        validateRefreshTokenMatches(refreshToken, savedRefreshToken);
    }

    private void validateRefreshTokenMatches(final String refreshToken, final String savedRefreshToken) {
        String encodedRefreshToken = hashEncoder.encodeSha256(refreshToken);

        if (!encodedRefreshToken.equals(savedRefreshToken)) {
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }
    }

    private void validateTokens(final String accessToken, final String refreshToken) {
        validateNotAccessTokenExpired(accessToken);
        validateRefreshTokenExpired(refreshToken);
    }

    private void validateRefreshTokenExpired(final String refreshToken) {
        Optional<Boolean> refreshTokenExpired = jwtProvider.isRefreshTokenExpired(refreshToken);

        if (refreshTokenExpired.isEmpty()) {
            throw new InvalidTokenException("인증 토큰이 올바르지 않습니다.");
        }

        if (Boolean.FALSE.equals(refreshTokenExpired.get())) {
            throw new InvalidTokenException("리프레시 토큰이 아직 만료되지 않았습니다.");
        }
    }

    private void validateNotAccessTokenExpired(final String accessToken) {
        Optional<Boolean> accessTokenExpired = jwtProvider.isAccessTokenExpired(accessToken);

        if (accessTokenExpired.isEmpty()) {
            throw new InvalidTokenException("인증 토큰이 올바르지 않습니다.");
        }

        if (Boolean.FALSE.equals(accessTokenExpired.get())) {
            throw new InvalidTokenException("엑세스 토큰이 아직 만료되지 않았습니다.");
        }
    }
}
