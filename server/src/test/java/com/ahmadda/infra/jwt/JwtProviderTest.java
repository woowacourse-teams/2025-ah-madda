package com.ahmadda.infra.jwt;

import com.ahmadda.infra.jwt.config.JwtProperties;
import com.ahmadda.infra.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private final String secretKey = UUID.randomUUID()
            .toString();
    private final Duration expiration = Duration.ofHours(1);
    private final JwtProperties jwtProperties = new JwtProperties(secretKey, expiration);

    private final JwtProvider sut = new JwtProvider(jwtProperties);

    @Test
    void JWT_토큰을_정상적으로_생성_및_검증_할_수_있다() {
        // given
        var memberId = 1L;
        var token = sut.createToken(memberId);

        // when
        var memberPayload = sut.parsePayload(token);

        // then
        assertThat(memberPayload.getMemberId()).isEqualTo(memberId);
    }

    @Test
    void 페이로드_변환시_토큰을_정상적으로_파싱한다() {
        // given
        var memberId = 2L;
        var token = sut.createToken(memberId);

        // when
        var payload = sut.parsePayload(token);

        // then
        assertThat(payload.getMemberId()).isEqualTo(memberId);
    }

    @Test
    void 페이로드_변환시_만료된_토큰일_경우_예외가_발생한다() {
        // given
        var now = Instant.now();
        var token = Jwts.builder()
                .claims(Jwts.claims()
                        .add("memberId", 3L)
                        .issuedAt(Date.from(now.minus(Duration.ofHours(2))))
                        .expiration(Date.from(now.minus(Duration.ofMinutes(1))))
                        .build())
                .signWith(jwtProperties.getSecretKey())
                .compact();

        // when // then
        assertThatThrownBy(() -> sut.parsePayload(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_잘못된_서명일_경우_예외가_발생한다() {
        // given
        var forgedKey = Keys.hmacShaKeyFor(UUID.randomUUID()
                .toString()
                .getBytes());

        var claims = Jwts.claims()
                .add("memberId", 4L)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now()
                        .plus(expiration)))
                .build();

        var token = Jwts.builder()
                .claims(claims)
                .signWith(forgedKey)
                .compact();

        // when // then
        assertThatThrownBy(() -> sut.parsePayload(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_빈_토큰이면_예외가_발생한다() {
        assertThatThrownBy(() -> sut.parsePayload(""))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_null_토큰이면_예외가_발생한다() {
        assertThatThrownBy(() -> sut.parsePayload(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_형식이_잘못된_토큰이면_예외가_발생한다() {
        assertThatThrownBy(() -> sut.parsePayload("this.is.not.jwt"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
