package com.ahmadda.infra.oauth.dto;

public record OAuthUserInfoResponse(
        String email,
        String name
) {

}
