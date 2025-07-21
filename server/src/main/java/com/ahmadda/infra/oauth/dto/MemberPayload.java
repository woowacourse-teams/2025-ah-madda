package com.ahmadda.infra.oauth.dto;

import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class MemberPayload {

    private final static String NAME_CLAIM_KEY = "name";
    private final static String EMAIL_CLAIM_KEY = "email";

    private final long memberId;
    private final String name;
    private final String getEmail;

    private MemberPayload(final long memberId, final String name, final String getEmail) {
        this.memberId = memberId;
        this.name = name;
        this.getEmail = getEmail;
    }

    public static MemberPayload create(final Claims claims) {
        long memberId = Long.parseLong(claims.getSubject());
        String name = claims.get(NAME_CLAIM_KEY, String.class);
        String email = claims.get(EMAIL_CLAIM_KEY, String.class);

        return new MemberPayload(memberId, name, email);
    }
}
