package com.ahmadda.infra.auth.jwt;

import com.ahmadda.common.exception.UnauthorizedException;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private JwtProvider sut;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        sut = new JwtProvider();
        secretKey = Keys.hmacShaKeyFor(UUID.randomUUID()
                .toString()
                .repeat(2)
                .getBytes());
    }

    @Test
    void 정상적으로_토큰을_생성하고_파싱할_수_있다() {
        // given
        Long memberId = 1L;
        var token = sut.createToken(memberId, Duration.ofHours(1), secretKey);

        // when
        JwtMemberPayload payload = sut.parsePayload(token, secretKey);

        // then
        assertThat(payload.memberId()).isEqualTo(memberId);
    }

    @Test
    void 유효하지_않은_토큰은_파싱시_예외가_발생한다() {
        // given
        var otherKey = Keys.hmacShaKeyFor(UUID.randomUUID()
                .toString()
                .repeat(2)
                .getBytes());
        var token = sut.createToken(3L, Duration.ofHours(1), otherKey);

        // when // then
        assertThatThrownBy(() -> sut.parsePayload(token, secretKey))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 인증 정보입니다.");
    }

    @Test
    void 만료된_토큰은_만료여부를_확인할_수_있다() {
        // given
        var now = Instant.now();
        var expiredToken = Jwts.builder()
                .claims(Jwts.claims()
                        .add("memberId", 2L)
                        .issuedAt(Date.from(now.minus(Duration.ofHours(2))))
                        .expiration(Date.from(now.minus(Duration.ofMinutes(1))))
                        .build())
                .signWith(secretKey)
                .compact();

        // when // then
        assertThat(sut.isTokenExpired(expiredToken, secretKey)).isTrue();
    }

    @Test
    void 유효하지_않은_토큰은_만료여부_검증시_예외가_발생한다() {
        var otherKey = Keys.hmacShaKeyFor(UUID.randomUUID()
                .toString()
                .repeat(2)
                .getBytes());
        var token = sut.createToken(4L, Duration.ofHours(1), otherKey);

        assertThatThrownBy(() -> sut.isTokenExpired(token, secretKey))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 인증 정보입니다.");
    }
}
