package com.ahmadda.infra;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.infra.config.JwtTokenProperties;
import com.ahmadda.presentation.dto.LoginMember;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@EnableConfigurationProperties(JwtTokenProperties.class)
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String NAME_ID_KEY = "name";

    private final JwtTokenProperties jwtTokenProperties;

    public String createToken(Claims claims, Duration duration) {
        Instant now = Instant.now();
        Instant expire = now.plus(duration);

        return Jwts.builder()
                .subject(claims.getSubject())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claims(claims)
                .signWith(jwtTokenProperties.getSecretKey())
                .compact();
    }

    public LoginMember extractLoginMember(final String token) {
        try {
            Claims claims = parseClaims(token);

            String memberId = claims.getSubject();
            String name = claims.get(NAME_ID_KEY, String.class);

            return new LoginMember(Long.parseLong(memberId), name);
        } catch (NumberFormatException e) {
            throw new BusinessRuleViolatedException("memberId가 올바르지 않습니다.");
        }
    }

    private Claims parseClaims(final String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(jwtTokenProperties.getSecretKey())
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
