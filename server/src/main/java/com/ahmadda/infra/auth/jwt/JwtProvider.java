package com.ahmadda.infra.auth.jwt;

import com.ahmadda.infra.auth.jwt.config.JwtProperties;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.auth.jwt.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;

@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

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

    private Claims parseClaims(final String token, final SecretKey secretKey) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException e) {
            throw new InvalidJwtException("잘못된 형식의 토큰입니다.", e);
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtException("만료기한이 지난 토큰입니다.", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException("인증 토큰을 파싱하는데 실패하였습니다.", e);
        }
    }
}
