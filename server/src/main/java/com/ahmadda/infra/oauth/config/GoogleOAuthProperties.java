package com.ahmadda.infra.oauth.config;

import com.ahmadda.infra.oauth.exception.GoogleOAuthPropertiesException;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
@Getter
public class GoogleOAuthProperties {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;
    private final String userUri;
    private final int connectTimeout;
    private final int readTimeout;

    public GoogleOAuthProperties(
            final String clientId,
            final String clientSecret,
            final String redirectUri,
            final String tokenUri,
            final String userUri,
            final int connectTimeout,
            final int readTimeout
    ) {
        validateProperties(clientId, clientSecret, redirectUri, tokenUri, userUri, connectTimeout, readTimeout);

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    private void validateProperties(
            final String clientId,
            final String clientSecret,
            final String redirectUri,
            final String tokenUri,
            final String userUri,
            final int connectTimeout,
            final int readTimeout
    ) {
        if (clientId == null || clientId.isBlank()) {
            throw new GoogleOAuthPropertiesException("Google OAuth clientId가 비어있습니다.");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new GoogleOAuthPropertiesException("Google OAuth clientSecret이 비어있습니다.");
        }
        if (redirectUri == null || redirectUri.isBlank()) {
            throw new GoogleOAuthPropertiesException("Google OAuth redirectUri가 비어있습니다.");
        }
        if (tokenUri == null || tokenUri.isBlank()) {
            throw new GoogleOAuthPropertiesException("Google OAuth tokenUri가 비어있습니다.");
        }
        if (userUri == null || userUri.isBlank()) {
            throw new GoogleOAuthPropertiesException("Google OAuth userUri가 비어있습니다.");
        }
        if (connectTimeout <= 0) {
            throw new GoogleOAuthPropertiesException("Google OAuth connectTimeout은 0보다 커야 합니다.");
        }
        if (readTimeout <= 0) {
            throw new GoogleOAuthPropertiesException("Google OAuth readTimeout은 0보다 커야 합니다.");
        }
    }
}
