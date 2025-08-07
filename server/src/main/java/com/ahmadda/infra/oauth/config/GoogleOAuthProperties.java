package com.ahmadda.infra.oauth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
@Getter
public class GoogleOAuthProperties {

    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;
    private final String userUri;
    private final int connectTimeout;
    private final int readTimeout;

    public GoogleOAuthProperties(
            final String clientId,
            final String clientSecret,
            final String tokenUri,
            final String userUri,
            final int connectTimeout,
            final int readTimeout
    ) {
        validateProperties(clientId, clientSecret, tokenUri, userUri, connectTimeout, readTimeout);

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    private void validateProperties(
            final String clientId,
            final String clientSecret,
            final String tokenUri,
            final String userUri,
            final int connectTimeout,
            final int readTimeout
    ) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Google OAuth clientId가 비어있습니다.");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("Google OAuth clientSecret이 비어있습니다.");
        }
        if (tokenUri == null || tokenUri.isBlank()) {
            throw new IllegalArgumentException("Google OAuth tokenUri가 비어있습니다.");
        }
        if (userUri == null || userUri.isBlank()) {
            throw new IllegalArgumentException("Google OAuth userUri가 비어있습니다.");
        }
        if (connectTimeout <= 0) {
            throw new IllegalArgumentException("Google OAuth connectTimeout은 0보다 커야 합니다.");
        }
        if (readTimeout <= 0) {
            throw new IllegalArgumentException("Google OAuth readTimeout은 0보다 커야 합니다.");
        }
    }
}
