package com.ahmadda.infra.jwt.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtTokenProperties {

    private final SecretKey secretKey;
    private final Duration accessExpiration;

    public JwtTokenProperties(final String secretKey, final Duration accessExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
    }
}
