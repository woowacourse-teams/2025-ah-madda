package com.ahmadda.infra.oauth.dto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;

@Getter
public class MemberPayload {

    private static final String MEMBER_ID_KEY = "memberId";

    private final Long memberId;

    private MemberPayload(final Long memberId) {
        this.memberId = memberId;
    }

    public static MemberPayload from(final Claims claims) {
        Long memberId = claims.get(MEMBER_ID_KEY, Long.class);

        return new MemberPayload(memberId);
    }

    public static Claims toClaims(final Long memberId) {
        return Jwts.claims()
                .add(MEMBER_ID_KEY, memberId)
                .build();
    }
}
