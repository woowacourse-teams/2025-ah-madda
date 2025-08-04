package com.ahmadda.infra.jwt;

import com.ahmadda.infra.jwt.config.JwtProperties;
import com.ahmadda.infra.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.jwt.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String createToken(final Long memberId) {
        Instant now = Instant.now();
        Instant expire = now.plus(jwtProperties.getAccessExpiration());

        Claims claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claims(claims)
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public JwtMemberPayload parsePayload(final String token) {
        Claims claims = parseClaims(token);

        return JwtMemberPayload.from(claims);
    }

    private Claims parseClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("jwtError : {} ", e.getMessage(), e);
            throw new InvalidJwtException("유효하지 않은 인증 정보입니다.", e);
        }
    }
}
