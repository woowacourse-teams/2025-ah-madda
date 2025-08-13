package com.ahmadda.infra.login.jwt.dto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;

@Getter
public class JwtMemberPayload {

    private static final String MEMBER_ID_KEY = "memberId";

    private final Long memberId;

    private JwtMemberPayload(final Long memberId) {
        this.memberId = memberId;
    }

    public static JwtMemberPayload from(final Claims claims) {
        Long memberId = claims.get(MEMBER_ID_KEY, Long.class);

        return new JwtMemberPayload(memberId);
    }

    public static Claims toClaims(final Long memberId) {
        return Jwts.claims()
                .add(MEMBER_ID_KEY, memberId)
                .build();
    }
}
