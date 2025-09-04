package com.ahmadda.infra.auth.oauth.dto;

public record OAuthUserInfoResponse(
        String email,
        String name,
        String picture
) {

}
