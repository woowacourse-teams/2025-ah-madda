package com.ahmadda.infra.jwt;

import com.ahmadda.domain.Member;
import com.ahmadda.infra.jwt.config.JwtTokenProperties;
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
@EnableConfigurationProperties(JwtTokenProperties.class)
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String NAME_ID_KEY = "name";
    private static final String EMAIL_ID_KEY = "email";

    private final JwtTokenProperties jwtTokenProperties;

    public String createToken(final Member member) {
        Instant now = Instant.now();
        Instant expire = now.plus(jwtTokenProperties.getAccessExpiration());

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

    public MemberPayload parsePayload(final String token) {
        Claims claims = parseClaims(token);

        long memberId = Long.parseLong(claims.getSubject());
        String name = claims.get(NAME_ID_KEY, String.class);
        String email = claims.get(EMAIL_ID_KEY, String.class);

        return new MemberPayload(memberId, name, email);
    }

    private Claims parseClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtTokenProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("jwtError : {} ", e.getMessage(), e);
            throw new InvalidTokenException("유효하지 않은 인증 정보입니다.");
        }
    }
}
