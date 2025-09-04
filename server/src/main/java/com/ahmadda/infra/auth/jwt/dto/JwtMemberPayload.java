package com.ahmadda.infra.auth.jwt.dto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class JwtMemberPayload {

    private static final String MEMBER_ID_KEY = "memberId";

    private final Long memberId;
    private final LocalDateTime expiresAt;

    private JwtMemberPayload(final Long memberId, final LocalDateTime expiresAt) {
        this.memberId = memberId;
        this.expiresAt = expiresAt;
    }

    public static JwtMemberPayload from(final Claims claims) {
        Long memberId = claims.get(MEMBER_ID_KEY, Long.class);

        LocalDateTime expiresAt = claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return new JwtMemberPayload(memberId, expiresAt);
    }

    public static Claims toClaims(final Long memberId) {
        return Jwts.claims()
                .add(MEMBER_ID_KEY, memberId)
                .build();
    }
}
