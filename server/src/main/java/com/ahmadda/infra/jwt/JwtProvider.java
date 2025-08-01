package com.ahmadda.infra.jwt;

import com.ahmadda.infra.jwt.config.JwtProperties;
import com.ahmadda.infra.jwt.exception.InvalidTokenException;
import com.ahmadda.infra.oauth.dto.MemberPayload;
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

        Claims claims = createAccessTokenClaims(memberId);

        return Jwts.builder()
                .subject(claims.getSubject())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claims(claims)
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public MemberPayload parsePayload(final String token) {
        Claims claims = parseClaims(token);

        return MemberPayload.create(claims);
    }

    private Claims createAccessTokenClaims(final Long memberId) {
        return Jwts.claims()
                .subject(memberId.toString())
                .build();
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
            throw new InvalidTokenException("유효하지 않은 인증 정보입니다.");
        }
    }
}
