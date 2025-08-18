package com.ahmadda.infra.login;

import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.infra.login.exception.InvalidTokenException;
import com.ahmadda.infra.login.jwt.JwtProvider;
import com.ahmadda.infra.login.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.login.util.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public MemberToken refreshMemberToken(final String accessToken,
                                          final String refreshToken,
                                          final String savedRefreshToken) {
        validateTokens(accessToken, refreshToken);

        JwtMemberPayload jwtMemberPayload = jwtProvider.parseRefreshPayload(refreshToken);
        Long memberId = jwtMemberPayload.getMemberId();

        validateRefreshTokenMatches(refreshToken, savedRefreshToken);

        return createMemberToken(memberId);
    }

    public void validateRefreshTokenMatch(final Long memberId,
                                          final String refreshToken,
                                          final String savedRefreshToken) {
        JwtMemberPayload payload = jwtProvider.parseRefreshPayload(refreshToken);
        if (!Objects.equals(memberId, payload.getMemberId())) {
            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
        }

        validateRefreshTokenMatches(refreshToken, savedRefreshToken);
    }

    private void validateRefreshTokenMatches(final String refreshToken, final String savedRefreshToken) {
        String encodedRefreshToken = HashUtils.sha256(refreshToken);

        if (!encodedRefreshToken.equals(savedRefreshToken)) {
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }
    }

    private void validateTokens(final String accessToken, final String refreshToken) {
        validateNotAccessTokenExpired(accessToken);
        validateRefreshTokenExpired(refreshToken);
    }

    private void validateRefreshTokenExpired(final String refreshToken) {
        if (jwtProvider.isRefreshTokenExpired(refreshToken)) {
            throw new InvalidTokenException("리프레시 토큰이 만료되었습니다.");
        }
    }

    private void validateNotAccessTokenExpired(final String accessToken) {
        if (!jwtProvider.isAccessTokenExpired(accessToken)) {
            throw new InvalidTokenException("엑세스 토큰이 만료되지 않았습니다.");
        }
    }
}
