package com.ahmadda.infra.jwt;

import com.ahmadda.infra.jwt.config.JwtTokenProperties;
import com.ahmadda.infra.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private String secretKey = UUID.randomUUID()
            .toString();
    private Duration expiration = Duration.ofHours(1);
    private JwtTokenProperties jwtTokenProperties = new JwtTokenProperties(secretKey, expiration);

    private JwtTokenProvider sut = new JwtTokenProvider(jwtTokenProperties);

    @Test
    void 페이로드_변환시_토큰을_반환한다() {
        // given
        var claims = createClaims(2L,
                                  "홍길동",
                                  "user@example.com",
                                  Instant.now(),
                                  Instant.now()
                                          .plus(expiration)
        );

        var token = Jwts.builder()
                .claims(claims)
                .signWith(jwtTokenProperties.getSecretKey())
                .compact();

        // when
        var payload = sut.parsePayload(token);

        // then
        assertThat(payload.getMemberId()).isEqualTo(2L);
    }

    @Test
    void 페이로드_변환시_만료된_토큰일시_예외가_발생한다() {
        // given
        var claims = createClaims(3L, "만료됨", "expired@example.com",
                                  Instant.now()
                                          .minus(Duration.ofHours(2)),
                                  Instant.now()
                                          .minus(Duration.ofMinutes(1))
        );

        var token = Jwts.builder()
                .claims(claims)
                .signWith(jwtTokenProperties.getSecretKey())
                .compact();

        // when // then
        assertThatThrownBy(() -> sut.parsePayload(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_잘못된_서명_예외가_발생한다() {
        // given
        var key = Keys.hmacShaKeyFor(UUID.randomUUID()
                                             .toString()
                                             .getBytes());

        var claims = createClaims(4L,
                                  "서명오류",
                                  "fail@example.com",
                                  Instant.now(),
                                  Instant.now()
                                          .plus(expiration)
        );

        var token = Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();

        // when // then
        assertThatThrownBy(() -> sut.parsePayload(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_빈_토큰_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.parsePayload(""))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_null_토큰_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.parsePayload(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 페이로드_변환시_형식_잘못된_토큰_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.parsePayload("this.is.not.jwt"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void JWT_토큰을_정상적으로_생성_및_검증_할_수_있다() {
        //given
        Long memberId = 1L;
        var token = sut.createToken(memberId);

        // when
        var memberPayload = sut.parsePayload(token);

        // then
        assertThat(memberPayload.getMemberId())
                .isEqualTo(memberId);
    }

    private Claims createClaims(Long memberId, String name, String email, Instant iat, Instant exp) {
        return Jwts.claims()
                .subject(memberId.toString())
                .add("name", name)
                .add("email", email)
                .issuedAt(Date.from(iat))
                .expiration(Date.from(exp))
                .build();
    }
}
