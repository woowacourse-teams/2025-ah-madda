package com.ahmadda.infra.auth.jwt.dto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record JwtMemberPayload(
        Long memberId,
        LocalDateTime expiresAt
) {

    private static final String MEMBER_ID_KEY = "memberId";

    public static JwtMemberPayload from(final Claims claims) {
        Long memberId = claims.get(MEMBER_ID_KEY, Long.class);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                claims.getExpiration()
                        .toInstant(),
                ZoneId.systemDefault()
        );

        return new JwtMemberPayload(memberId, expiresAt);
    }

    public static Claims toClaims(final Long memberId) {
        return Jwts.claims()
                .add(MEMBER_ID_KEY, memberId)
                .build();
    }
}
