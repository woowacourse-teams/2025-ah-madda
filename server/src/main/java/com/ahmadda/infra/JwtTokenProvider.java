package com.ahmadda.infra;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey; // JWT 서명에 사용할 키

    @Value("${jwt.expiration-ms}") // JWT 유효 시간 (밀리초)
    private long jwtExpirationMs;

    public JwtTokenProvider(@Value("${jwt.secret-key}") final String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes()); // HMAC-SHA 알고리즘에 맞는 키 생성
    }

    public String createToken(final String name, final String email) {
        return Jwts.builder()
                .subject(email)
                .claim("email", email)
                .claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }
}
