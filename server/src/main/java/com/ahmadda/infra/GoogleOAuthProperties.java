package com.ahmadda.infra;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "google.oauth2")
@Getter
public class GoogleOAuthProperties {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;
    private final String userUri;
    private final int connectTimeout;
    private final int readTimeout;

    @ConstructorBinding
    public GoogleOAuthProperties(String clientId,
                                 String clientSecret,
                                 String redirectUri,
                                 String tokenUri,
                                 String userUri,
                                 int connectTimeout,
                                 int readTimeout) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }
}