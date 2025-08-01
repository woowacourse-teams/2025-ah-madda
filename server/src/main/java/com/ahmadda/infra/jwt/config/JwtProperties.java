package com.ahmadda.infra.jwt.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {

    private final SecretKey secretKey;
    private final Duration accessExpiration;

    public JwtProperties(final String secretKey, final Duration accessExpiration) {
        validateProperties(secretKey, accessExpiration);

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
    }

    private void validateProperties(final String secretKey, final Duration accessExpiration) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("JWT 시크릿 키가 비어있습니다.");
        }
        if (accessExpiration == null) {
            throw new IllegalArgumentException("JWT 액세스 만료 시간이 지정되지 않았습니다.");
        }
        if (accessExpiration.isNegative() || accessExpiration.isZero()) {
            throw new IllegalArgumentException("JWT 액세스 만료 시간은 0보다 커야 합니다.");
        }
    }
}
