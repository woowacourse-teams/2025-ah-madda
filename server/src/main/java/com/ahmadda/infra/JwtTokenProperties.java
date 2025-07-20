package com.ahmadda.infra;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
@Getter
public class JwtTokenProperties {

    private final SecretKey secretKey;
    private final Duration accessExpirationDay;

    public JwtTokenProperties(String secretKey, long accessExpirationDay) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationDay = Duration.ofDays(accessExpirationDay);
    }
}
