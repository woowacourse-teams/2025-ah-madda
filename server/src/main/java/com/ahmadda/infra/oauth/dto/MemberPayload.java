package com.ahmadda.infra.oauth.dto;

import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class MemberPayload {

    private final long memberId;

    private MemberPayload(final long memberId) {
        this.memberId = memberId;
    }

    public static MemberPayload create(final Claims claims) {
        long memberId = Long.parseLong(claims.getSubject());

        return new MemberPayload(memberId);
    }
}
