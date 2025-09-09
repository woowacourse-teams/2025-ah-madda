package com.ahmadda.infra.auth.jwt;

import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    static JwtAccessTokenProperties jwtAccessTokenProperties;
    static JwtRefreshTokenProperties jwtRefreshProperties;
    static JwtProvider sut;

    @BeforeAll
    static void setUpAll() {
        String accessSecretKey = UUID.randomUUID()
                .toString();
        String refreshSecretKey = UUID.randomUUID()
                .toString();
        Duration accessExpiration = Duration.ofHours(1);
        Duration refreshExpiration = Duration.ofHours(1);

        jwtAccessTokenProperties = new JwtAccessTokenProperties(
                accessSecretKey, accessExpiration
        );
        jwtRefreshProperties = new JwtRefreshTokenProperties(
                refreshSecretKey, refreshExpiration
        );

        sut = new JwtProvider();
    }

    @Test
    void JWT_토큰을_정상적으로_생성_및_검증_할_수_있다() {
        // given
        var memberId = 1L;
        var token = sut.createToken(memberId,
                                    jwtAccessTokenProperties.getAccessExpiration(),
                                    jwtAccessTokenProperties.getAccessSecretKey()
        );

        // when
        var memberPayload = sut.parsePayload(token, jwtAccessTokenProperties.getAccessSecretKey());

        // then
        assertThat(memberPayload.get()
                           .getMemberId()).isEqualTo(memberId);
    }

    @Test
    void 페이로드_변환시_만료된_토큰일_경우_옵셔널_빈을_반환한다() {
        // given
        var now = Instant.now();
        var token = Jwts.builder()
                .claims(Jwts.claims()
                                .add("memberId", 3L)
                                .issuedAt(Date.from(now.minus(Duration.ofHours(2))))
                                .expiration(Date.from(now.minus(Duration.ofMinutes(1))))
                                .build())
                .signWith(jwtAccessTokenProperties.getAccessSecretKey())
                .compact();

        // when // then
        assertThat(sut.parsePayload(token, jwtAccessTokenProperties.getAccessSecretKey())).isEmpty();
    }

    @Test
    void 페이로드_변환시_잘못된_서명일_경우_예외가_발생한다() {
        // given
        var forgedKey = Keys.hmacShaKeyFor(UUID.randomUUID()
                                                   .toString()
                                                   .getBytes());

        var expiration = Duration.ofHours(1);

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
        assertThat(sut.parsePayload(token, jwtAccessTokenProperties.getAccessSecretKey())).isEmpty();
    }

    @Test
    void 페이로드_변환시_빈_토큰이면_빈_옵셔널을_반환한다() {
        assertThat(sut.parsePayload("", jwtAccessTokenProperties.getAccessSecretKey())).isEmpty();
    }

    @Test
    void 페이로드_변환시_null_토큰이면_빈_옵셔널을_반환한다() {
        assertThat(sut.parsePayload(null, jwtAccessTokenProperties.getAccessSecretKey())).isEmpty();
    }

    @Test
    void 페이로드_변환시_형식이_잘못된_토큰이면_빈_옵셔널을_반환한다() {
        assertThat(sut.parsePayload("this is not jwt", jwtAccessTokenProperties.getAccessSecretKey())).isEmpty();
    }

    @Test
    void 토큰_만료_여부를_확인할_수_있다() {
        // given
        var now = Instant.now();
        var expiredAccessToken = Jwts.builder()
                .claims(Jwts.claims()
                                .add("memberId", 50L)
                                .issuedAt(Date.from(now.minus(Duration.ofHours(2))))
                                .expiration(Date.from(now.minus(Duration.ofMinutes(1))))
                                .build())
                .signWith(jwtAccessTokenProperties.getAccessSecretKey())
                .compact();

        //when // then
        assertThat(sut.isTokenExpired(expiredAccessToken, jwtAccessTokenProperties.getAccessSecretKey())
                           .get()).isTrue();
    }

    @Test
    void 토큰_만료여부에서_올바르지_않으면_빈_옵셔널을_반환한다() {
        var invalidToken = "invalidAccessToken";

        //when // then
        assertThat(sut.isTokenExpired(invalidToken, jwtAccessTokenProperties.getAccessSecretKey())).isEmpty();
    }


    @Test
    void 토큰을_올바르지_않은_키로_검증시_빈_옵셔널을_반환한다() {
        // given
        var accessToken = sut.createToken(60L,
                                          jwtAccessTokenProperties.getAccessExpiration(),
                                          jwtAccessTokenProperties.getAccessSecretKey()
        );

        // when // then
        assertThat(sut.isTokenExpired(accessToken, jwtRefreshProperties.getRefreshSecretKey())).isEmpty();

    }
}
