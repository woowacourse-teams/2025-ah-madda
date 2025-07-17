package com.ahmadda.application;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {

}
