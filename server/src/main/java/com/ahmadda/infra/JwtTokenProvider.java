package com.ahmadda.infra;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.presentation.LoginMember;
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
    private final Duration accessExpirationDay;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String jwtSecretKey,
            @Value("${jwt.access-expiration-day}") long accessExpirationDay
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationDay = Duration.ofDays(accessExpirationDay);
    }

    public String createAccessToken(final Member member) {
        Instant now = Instant.now();
        Instant expire = now.plus(accessExpirationDay);

        String memberId = member.getId().toString();

        return Jwts.builder()
                .subject(memberId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claim("name", member.getName())
                .signWith(secretKey)
                .compact();
    }

    public LoginMember extractLoginMember(final String token) {
        try {
            Claims claims = parseClaims(token);

            String memberId = claims.getSubject();
            String name = claims.get("name", String.class);

            return new LoginMember(Long.parseLong(memberId), name);
        } catch (NumberFormatException e) {
            throw new BusinessRuleViolatedException("memberId가 옳바르지 않습니다.");
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
