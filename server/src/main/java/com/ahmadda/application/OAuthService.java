package com.ahmadda.application;

import com.ahmadda.infra.GoogleOAuthProperties;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuthProperties googleOAuthProperties;

    public String generateGoogleAuthUrl(String state){
        return UriComponentsBuilder.fromUriString(googleOAuthProperties.getAuthorizationUri())
                .queryParam("client_id", googleOAuthProperties.getClientId())
                .queryParam("redirect_uri", googleOAuthProperties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", googleOAuthProperties.getScope())
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .build().toUriString();
    }
}
