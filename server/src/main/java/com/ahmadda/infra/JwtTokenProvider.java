package com.ahmadda.infra;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.infra.config.JwtTokenProperties;
import com.ahmadda.infra.dto.JwtPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
    private static final String EMAIL_ID_KEY = "email";

    private final JwtTokenProperties jwtTokenProperties;

    public String createToken(final Member member, final Duration duration) {
        Instant now = Instant.now();
        Instant expire = now.plus(duration);

        Claims claims = createAccessTokenClaims(member);

        return Jwts.builder()
                .subject(claims.getSubject())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claims(claims)
                .signWith(jwtTokenProperties.getSecretKey())
                .compact();
    }

    private Claims createAccessTokenClaims(final Member member) {
        Long memberId = member.getId();

        return Jwts.claims()
                .subject(memberId.toString())
                .build();
    }

    public JwtPayload parsePayload(final String token) {
        Claims claims = parseClaims(token);

        long memberId = Long.parseLong(claims.getSubject());
        String name = claims.get(NAME_ID_KEY, String.class);
        String email = claims.get(EMAIL_ID_KEY, String.class);

        return new JwtPayload(memberId, name, email);
    }

    private Claims parseClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtTokenProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessRuleViolatedException("유효하지 않은 토큰입니다.");
        }
    }
}
