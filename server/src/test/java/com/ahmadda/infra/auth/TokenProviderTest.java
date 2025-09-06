package com.ahmadda.infra.auth;

import com.ahmadda.infra.auth.exception.InvalidTokenException;
import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtProperties;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TokenProviderTest {

    static JwtProperties jwtProperties;

    static JwtProvider jwtProvider;

    static HashEncoder hashEncoder;

    static TokenProvider sut;

    @BeforeAll
    static void setUpAll() {
        String accessSecretKey = UUID.randomUUID()
                .toString();
        String refreshSecretKey = UUID.randomUUID()
                .toString();
        Duration accessExpiration = Duration.ofHours(1);
        Duration refreshExpiration = Duration.ofHours(1);

        jwtProperties = new JwtProperties(
                accessSecretKey, accessExpiration,
                refreshSecretKey, refreshExpiration
        );
        jwtProvider = new JwtProvider(jwtProperties);
        hashEncoder = new HashEncoder();

        sut = new TokenProvider(jwtProvider, hashEncoder);
    }

    @Test
    void 액세스와_리프레시_토큰을_생성할_수_있다() {
        // given
        var memberId = 1L;

        // when // then
        assertDoesNotThrow(() -> sut.createMemberToken(memberId));
    }

    @Test
    void 재발급_시_액세스_토큰이_만료되지_않으면_예외가_발생한다() {
        // given
        var memberId = 1L;

        var savedToken = sut.createMemberToken(memberId);
        var memberToken = sut.createMemberToken(memberId);

        // when // then
        assertThatThrownBy(() -> sut.refreshMemberToken(memberToken.accessToken(),
                                                        memberToken.refreshToken(),
                                                        savedToken.refreshToken()
                           )
        ).isInstanceOf(InvalidTokenException.class)
                .hasMessage("엑세스 토큰이 만료되지 않았습니다.");
    }

    @Test
    void 재발급_시_리프레시_토큰이_만료되었으면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();

        var expiredAccessToken = createExpiredAccessToken(memberId);
        var expiredRefreshToken = createExpiredRefreshToken(memberId);

        // when // then
        assertThatThrownBy(() -> sut.refreshMemberToken(expiredAccessToken, expiredRefreshToken, userAgent))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("리프레시 토큰이 만료되었습니다.");
    }

    @Test
    void 리프레시_토큰이_유효하면_토큰을_지울_수_있다() {
        // given
        var memberId = 1L;
        var memberToken = sut.createMemberToken(memberId);

        var userAgent = createUserAgent();
        var expiresAt = LocalDateTime.now()
                .plusDays(1);

        var encodedToken = hashEncoder.encodeSha256(memberToken.refreshToken());
        var deviceId = hashEncoder.encodeSha256(userAgent);

        var savedRefreshToken = RefreshToken.create(encodedToken, memberId, deviceId, expiresAt);
        // when // then
        assertDoesNotThrow(() -> sut.validateRefreshTokenMatch(memberToken.refreshToken(),
                                                               savedRefreshToken.getToken(),
                                                               memberId
        ));
    }

    @Test
    void 액세스_토큰과_리프레시_토큰_주인이_다르면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var otherMemberId = 2L;

        var memberToken = sut.createMemberToken(memberId);
        var otherToken = sut.createMemberToken(otherMemberId);

        // when // then
        assertThatThrownBy(() -> sut.validateRefreshTokenMatch(memberToken.refreshToken(),
                                                               otherToken.refreshToken(),
                                                               otherMemberId
        ))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("토큰 정보가 일치하지 않습니다.");
    }

    private String createUserAgent() {
        return UUID.randomUUID()
                .toString();
    }

    private String createExpiredAccessToken(Long memberId) {
        var now = Instant.now();

        var claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.minus(Duration.ofDays(1))))
                .claims(claims)
                .signWith(jwtProperties.getAccessSecretKey())
                .compact();
    }

    private String createExpiredRefreshToken(Long memberId) {
        var now = Instant.now();

        return Jwts.builder()
                .claims(JwtMemberPayload.toClaims(memberId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.minus(Duration.ofDays(1))))
                .signWith(jwtProperties.getRefreshSecretKey())
                .compact();
    }
}
