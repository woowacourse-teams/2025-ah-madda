package com.ahmadda.infra.auth.jwt;

import com.ahmadda.common.exception.UnauthorizedException;
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

    public JwtMemberPayload parsePayload(final String token, final SecretKey secretKey) {
        Claims claims = parseClaims(token, secretKey);
        
        return JwtMemberPayload.from(claims);
    }

    public boolean isTokenExpired(final String token, final SecretKey secretKey) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 인증 정보입니다.", e);
        }
    }

    private Claims parseClaims(final String token, final SecretKey secretKey) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 인증 정보입니다.", e);
        }
    }
}
