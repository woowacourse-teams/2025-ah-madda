package com.ahmadda.infra.jwt;

import com.ahmadda.infra.jwt.config.JwtTokenProperties;
import com.ahmadda.infra.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider sut;

    @MockitoBean
    private JwtTokenProperties jwtTokenProperties;

    private SecretKey secretKey;
    private Duration expiration;

    @BeforeEach
    void setUp() {
        secretKey = Keys.hmacShaKeyFor(UUID.randomUUID()
                                               .toString()
                                               .getBytes());
        expiration = Duration.ofHours(1);
        given(jwtTokenProperties.getSecretKey()).willReturn(secretKey);
        given(jwtTokenProperties.getAccessExpiration()).willReturn(expiration);
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

    @Test
    void parsePayload_정상_토큰() {
        var claims = createClaims(2L,
                                  "홍길동",
                                  "user@example.com",
                                  Instant.now(),
                                  Instant.now()
                                          .plus(expiration)
        );

        var token = Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();

        var payload = sut.parsePayload(token);

        assertSoftly(softly -> {
            softly.assertThat(payload.getMemberId())
                    .isEqualTo(2L);
            softly.assertThat(payload.getName())
                    .isEqualTo("홍길동");
            softly.assertThat(payload.getEmail())
                    .isEqualTo("user@example.com");
        });
    }

    @Test
    void parsePayload_만료된_토큰_예외() {
        var claims = createClaims(3L, "만료됨", "expired@example.com",
                                  Instant.now()
                                          .minus(Duration.ofHours(2)),
                                  Instant.now()
                                          .minus(Duration.ofMinutes(1))
        );

        var token = Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();

        assertThatThrownBy(() -> sut.parsePayload(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parsePayload_잘못된_서명_예외() {
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

        assertThatThrownBy(() -> sut.parsePayload(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parsePayload_빈_토큰_예외() {
        assertThatThrownBy(() -> sut.parsePayload(""))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parsePayload_null_토큰_예외() {
        assertThatThrownBy(() -> sut.parsePayload(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parsePayload_형식_잘못된_토큰_예외() {
        assertThatThrownBy(() -> sut.parsePayload("this.is.not.jwt"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
