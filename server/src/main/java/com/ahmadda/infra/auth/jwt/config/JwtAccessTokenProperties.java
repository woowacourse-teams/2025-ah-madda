package com.ahmadda.infra.auth.jwt.config;

import io.jsonwebtoken.security.Keys;
import jdk.jfr.Name;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ConfigurationProperties(prefix = "jwt.access")
@Getter
public class JwtAccessTokenProperties {

    private final SecretKey accessSecretKey;
    private final Duration accessExpiration;

    public JwtAccessTokenProperties(final String secretKey, final Duration expiration) {
        validateProperties(secretKey, expiration);

        this.accessSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = expiration;
    }

    private void validateProperties(final String secretKey, final Duration accessExpiration) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("JWT 시크릿 키가 비어있습니다.");
        }
        if (accessExpiration == null) {
            throw new IllegalArgumentException("JWT 만료 시간이 지정되지 않았습니다.");
        }
        if (accessExpiration.isNegative() || accessExpiration.isZero()) {
            throw new IllegalArgumentException("JWT 만료 시간은 0보다 커야 합니다.");
        }
    }
}
