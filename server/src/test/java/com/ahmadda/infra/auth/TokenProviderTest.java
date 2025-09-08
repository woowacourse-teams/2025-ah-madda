package com.ahmadda.infra.auth;

import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

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

        sut = new TokenProvider(jwtProvider);
    }

    @Test
    void 액세스와_리프레시_토큰을_생성할_수_있다() {
        // given
        var memberId = 1L;

        // when // then
        assertDoesNotThrow(() -> sut.createMemberToken(memberId));
    }
}
