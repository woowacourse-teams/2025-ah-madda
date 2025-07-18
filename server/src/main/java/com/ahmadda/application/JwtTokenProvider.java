package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
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

    public AuthTokens publishLoginTokens(final Member member) {
        String refreshToken = createRefreshToken(member);
        String accessToken = createAccessToken(member);

        return new AuthTokens(refreshToken, accessToken);
    }

    private String createRefreshToken(final Member member) {
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

    public String createAccessToken(final Member member) {
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

    public long extractId(final String token) {
        try {
            String memberId = parseClaims(token).getSubject();

            return Long.parseLong(memberId);
        } catch (NumberFormatException e) {
            throw new BusinessRuleViolatedException("");
        }
    }

    private Claims parseClaims(final String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = jws.getPayload();

            Date expiration = claims.getExpiration();
            Date now = new Date();

            if (expiration.before(now)) {
                throw new BusinessRuleViolatedException("토큰이 만료되었습니다.");
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessRuleViolatedException("유효하지 않은 토큰입니다.");
        }
    }
}
