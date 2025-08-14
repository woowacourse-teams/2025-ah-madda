package com.ahmadda.infra.login.jwt;

import com.ahmadda.infra.login.jwt.config.JwtProperties;
import com.ahmadda.infra.login.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.login.jwt.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String createAccessToken(final Long memberId) {
        Instant now = Instant.now();
        Instant expire = now.plus(jwtProperties.getAccessExpiration());

        Claims claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claims(claims)
                .signWith(jwtProperties.getAccessSecretKey())
                .compact();
    }

    public String createRefreshToken(final Long memberId) {
        Instant now = Instant.now();
        Instant expire = now.plus(jwtProperties.getRefreshExpiration());

        Claims claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .claims(claims)
                .signWith(jwtProperties.getRefreshSecretKey())
                .compact();
    }

    public JwtMemberPayload parseAccessPayload(final String accessToken) {
        Claims claims = parseClaims(accessToken, jwtProperties.getAccessSecretKey());

        return JwtMemberPayload.from(claims);
    }

    public JwtMemberPayload parseRefreshPayload(final String refreshToken) {
        Claims claims = parseClaims(refreshToken, jwtProperties.getRefreshSecretKey());

        return JwtMemberPayload.from(claims);
    }

    public boolean isAccessTokenExpired(final String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(jwtProperties.getAccessSecretKey())
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
            return false;
        } catch (ExpiredJwtException e) {
            return true;
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
            log.error("jwtError : {} ", e.getMessage(), e);
            throw new InvalidJwtException("인증 토큰을 파싱하는데 실패하였습니다.", e);
        }
    }
}
