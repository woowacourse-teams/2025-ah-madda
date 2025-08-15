package com.ahmadda.infra.login;

import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.infra.login.exception.InvalidTokenException;
import com.ahmadda.infra.login.jwt.JwtProvider;
import com.ahmadda.infra.login.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.login.util.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberToken createMemberToken(final Long memberId, final String userAgent) {
        String accessToken = jwtProvider.createAccessToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        JwtMemberPayload jwtMemberPayload = jwtProvider.parseRefreshPayload(refreshToken);
        LocalDateTime expireAt = jwtMemberPayload.getExpireAt();

        String deviceId = HashUtils.sha256(userAgent);
        refreshTokenRepository.deleteByMemberIdAndDeviceId(memberId, deviceId);

        String encodedRefreshToken = passwordEncoder.encode(refreshToken);

        RefreshToken refreshTokenEntity =
                RefreshToken.create(encodedRefreshToken, memberId, deviceId, expireAt);
        refreshTokenRepository.save(refreshTokenEntity);

        return new MemberToken(accessToken, refreshToken);
    }

    public MemberToken renewMemberToken(final String accessToken, final String refreshToken, final String userAgent) {
        validateAccessTokenExpired(accessToken);

        JwtMemberPayload jwtMemberPayload = jwtProvider.parseRefreshPayload(refreshToken);
        Long memberId = jwtMemberPayload.getMemberId();

        String deviceId = HashUtils.sha256(userAgent);
        RefreshToken savedRefreshToken = getRefreshToken(memberId, deviceId);

        validateRefreshTokenMatches(refreshToken, savedRefreshToken.getToken());

        refreshTokenRepository.delete(savedRefreshToken);

        return createMemberToken(memberId, userAgent);
    }

    public void deleteRefreshToken(final Long memberId, final String refreshToken, final String userAgent) {
        JwtMemberPayload payload = jwtProvider.parseRefreshPayload(refreshToken);
        if (!Objects.equals(memberId, payload.getMemberId())) {
            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
        }

        String deviceId = HashUtils.sha256(userAgent);
        RefreshToken savedRefreshToken = getRefreshToken(memberId, deviceId);

        validateRefreshTokenMatches(refreshToken, savedRefreshToken.getToken());

        refreshTokenRepository.delete(savedRefreshToken);
    }

    private void validateRefreshTokenMatches(final String refreshToken, final String savedRefreshToken) {
        if (!passwordEncoder.matches(refreshToken, savedRefreshToken)) {
            throw new InvalidTokenException("리프레시 토큰이 일치하지 않습니다.");
        }
    }

    private RefreshToken getRefreshToken(final Long memberId, final String deviceId) {
        return refreshTokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)
                .orElseThrow(() -> new InvalidTokenException("토큰을 찾을 수 없습니다."));
    }

    private void validateAccessTokenExpired(final String accessToken) {
        if (!jwtProvider.isAccessTokenExpired(accessToken)) {
            throw new InvalidTokenException("아직 만료되지 않은 액세스 토큰입니다.");
        }
    }
}
