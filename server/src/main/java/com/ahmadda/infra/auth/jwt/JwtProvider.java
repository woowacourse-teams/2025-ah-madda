package com.ahmadda.infra.auth.jwt;

import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    public String createToken(final Long memberId, final Duration duration, final SecretKey secretKey) {
        Instant now = Instant.now();
        Instant expire = now.plus(duration);

        Claims claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(secretKey)
                .compact();
    }

    public Optional<JwtMemberPayload> parsePayload(final String token, final SecretKey secretKey) {
        return parseClaims(token, secretKey)
                .map(JwtMemberPayload::from);
    }

    public Optional<Boolean> isTokenExpired(final String token, final SecretKey secretKey) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(false);
        } catch (ExpiredJwtException e) {
            return Optional.of(true);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<Claims> parseClaims(final String token, final SecretKey secretKey) {
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(payload);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
