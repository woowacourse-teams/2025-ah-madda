package com.ahmadda.application;

import com.ahmadda.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String jwtSecretKey,
            @Value("${jwt.access-expiration}") long accessExpirationTime,
            @Value("${jwt.refresh-expiration}") long refreshExpirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofDays(refreshExpirationTime);
        this.refreshExpiration = Duration.ofHours(accessExpirationTime);
    }

    public AuthTokens publishLoginTokens(Member member) {
        String refreshToken = createRefreshToken(member);
        String accessToken = createAccessToken(member);

        return new AuthTokens(refreshToken, accessToken);
    }

    private String createRefreshToken(Member member) {
        Instant now = Instant.now();
        Instant expire = now.plus(refreshExpiration);

        String memberId = member.getId().toString();

        return Jwts.builder()
                .subject(memberId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(secretKey)
                .compact();
    }

    private String createAccessToken(Member member) {
        Instant now = Instant.now();
        Instant expire = now.plus(accessExpiration);

        String memberId = member.getId().toString();

        return Jwts.builder()
                .subject(memberId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(secretKey)
                .compact();
    }
}
