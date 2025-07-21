package com.ahmadda.infra.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtTokenProperties {

    private final SecretKey secretKey;

    public JwtTokenProperties(final String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
