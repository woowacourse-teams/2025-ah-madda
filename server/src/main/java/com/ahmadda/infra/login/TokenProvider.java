package com.ahmadda.infra.login;

import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.infra.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public MemberToken getMemberToken(Long memberId) {
        String accessToken = jwtProvider.createAccessToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        RefreshToken refreshTokenEntity = RefreshToken.create(refreshToken, memberId);

        RefreshToken saveRefreshToken = refreshTokenRepository.save(refreshTokenEntity);

        return new MemberToken(accessToken, saveRefreshToken.getToken());
    }
}
