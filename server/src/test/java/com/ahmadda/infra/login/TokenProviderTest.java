package com.ahmadda.infra.login;

import com.ahmadda.infra.login.exception.InvalidTokenException;
import com.ahmadda.infra.login.jwt.config.JwtProperties;
import com.ahmadda.infra.login.jwt.dto.JwtMemberPayload;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "jwt.access-secret-key=0123456789abcdef0123456789abcdef",
                "jwt.refresh-secret-key=abcdef0123456789abcdef0123456789",
                "jwt.access-expiration=10d",
                "jwt.refresh-expiration=10d"
        }
)
@Transactional
class TokenProviderTest {

    @Autowired
    TokenProvider sut;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    JwtProperties jwtProperties;

    @Test
    void 액세스와_리프레시_토큰을_생성할_수_잇다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();

        // when // then
        Assertions.assertDoesNotThrow(() -> sut.createMemberToken(memberId, userAgent));
    }

    @Test
    void 재발급_시_액세스_토큰이_만료되지_않으면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();
        var memberToken = sut.createMemberToken(memberId, userAgent);

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken(memberToken.accessToken(), memberToken.refreshToken(), userAgent))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("아직 만료되지 않은 액세스 토큰입니다.");
    }

    @Test
    void 액세스_토큰이_만료되었으면_재발급_받을_수_있다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();
        var memberToken = sut.createMemberToken(memberId, userAgent);

        var expiredAccessToken = createExpiredAccessToken(memberId);

        // when // then
        Assertions.assertDoesNotThrow(() -> sut.renewMemberToken(expiredAccessToken,
                                                                 memberToken.refreshToken(),
                                                                 userAgent
        ));
    }

    @Test
    void 재발급_시_리프레시_토큰이_만료되었으면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();

        var expiredAccessToken = createExpiredAccessToken(memberId);
        var expiredRefreshToken = createExpiredRefreshToken(memberId, userAgent);

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken(expiredAccessToken, expiredRefreshToken, userAgent))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("리프레시 토큰이 만료되었습니다.");
    }

    @Test
    void 재발급_시_유저_에이전트가_다르면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();
        var otherUserAgent = createUserAgent();
        var memberToken = sut.createMemberToken(memberId, userAgent);

        var expiredAccessToken = createExpiredAccessToken(memberId);

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken(expiredAccessToken,
                                                      memberToken.refreshToken(),
                                                      otherUserAgent
        ))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("토큰을 찾을 수 없습니다.");
    }

    @Test
    void 리프레시_토큰이_유효하면_토큰을_지울_수_있다() {
        // given
        var memberId = 1L;
        var userAgent = createUserAgent();
        var memberToken = sut.createMemberToken(memberId, userAgent);

        // when
        sut.deleteRefreshToken(memberId, memberToken.refreshToken(), userAgent);

        // then
        assertThat(refreshTokenRepository.findAll()).hasSize(0);
    }

    @Test
    void 액세스_토큰과_리프레시_토큰_주인이_다르면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var otherMemberId = 2L;

        var userAgent = createUserAgent();

        var memberToken = sut.createMemberToken(memberId, userAgent);

        // when // then
        assertThatThrownBy(() -> sut.deleteRefreshToken(otherMemberId, memberToken.refreshToken(), userAgent))
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

    private String createExpiredRefreshToken(Long memberId, String userAgent) {
        var now = Instant.now();

        String token = Jwts.builder()
                .claims(JwtMemberPayload.toClaims(memberId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.minus(Duration.ofDays(1)))) // 이미 만료
                .signWith(jwtProperties.getRefreshSecretKey())
                .compact();

        LocalDateTime expiredAt = LocalDateTime.now()
                .minusDays(1); // 레포에도 만료로 기록

        refreshTokenRepository.save(
                RefreshToken.create(token, memberId, userAgent, expiredAt)
        );

        return token;
    }
}
