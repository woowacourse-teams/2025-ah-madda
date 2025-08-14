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

    public MemberToken getMemberToken(final Long memberId) {
        String accessToken = jwtProvider.createAccessToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        RefreshToken refreshTokenEntity = RefreshToken.create(refreshToken, memberId);
        RefreshToken saveRefreshToken = refreshTokenRepository.save(refreshTokenEntity);

        return new MemberToken(accessToken, saveRefreshToken.getToken());
    }

    public void deleteRefreshToken(final Long memberId) {
        RefreshToken refreshToken = getRefreshToken(memberId);

        refreshTokenRepository.delete(refreshToken);
    }

    private RefreshToken getRefreshToken(final Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new InvalidTokenException("토큰을 찾을 수 없습니다."));
    }
}
