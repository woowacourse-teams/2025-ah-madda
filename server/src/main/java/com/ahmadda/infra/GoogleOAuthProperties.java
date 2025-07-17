package com.ahmadda.infra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.oauth2")
@Getter
@RequiredArgsConstructor
public class GoogleOAuthProperties {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String scope;
    private final String authorizationUri;
    private final String tokenUri;
    private final String userInfoUri;
}
