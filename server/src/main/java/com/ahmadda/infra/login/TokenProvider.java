package com.ahmadda.infra.login;

import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.infra.login.exception.InvalidTokenException;
import com.ahmadda.infra.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberToken createMemberToken(final Long memberId) {
        String accessToken = jwtProvider.createAccessToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        String encodedRefreshToken = passwordEncoder.encode(refreshToken);

        RefreshToken refreshTokenEntity = RefreshToken.create(encodedRefreshToken, memberId);
        refreshTokenRepository.save(refreshTokenEntity);

        return new MemberToken(accessToken, refreshToken);
    }

    public MemberToken renewMemberToken(final String accessToken, final String refreshToken) {
        validateAccessTokenExpired(accessToken);
        
        Long memberId = jwtProvider.parseRefreshPayload(refreshToken)
                .getMemberId();

        RefreshToken saved = getRefreshToken(memberId);
        validateRefreshTokenMatches(refreshToken, saved.getToken());

        deleteRefreshToken(memberId);

        return createMemberToken(memberId);
    }

    public void deleteRefreshToken(final Long memberId) {
        RefreshToken refreshToken = getRefreshToken(memberId);

        refreshTokenRepository.delete(refreshToken);
    }

    private RefreshToken getRefreshToken(final Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new InvalidTokenException("토큰을 찾을 수 없습니다."));
    }

    private void validateAccessTokenExpired(final String accessToken) {
        if (!jwtProvider.isAccessTokenExpired(accessToken)) {
            throw new InvalidTokenException("아직 만료되지 않은 액세스 토큰입니다.");
        }
    }

    private void validateRefreshTokenMatches(final String refreshToken, final String encodedRefreshToken) {
        if (!passwordEncoder.matches(refreshToken, encodedRefreshToken)) {
            throw new InvalidTokenException("리프레시 토큰이 일치하지 않습니다.");
        }
    }
}
