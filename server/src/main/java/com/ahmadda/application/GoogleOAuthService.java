package com.ahmadda.application;

import com.ahmadda.infra.GoogleOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final GoogleOAuthProperties googleOAuthProperties;
    private final RestClient restClient;

    public GoogleOAuthUserInfo authenticateGoogleUser(String code) {
        String accessToken = exchangeCodeForAccessToken(code);
        return getUserInfo(accessToken);
    }

    private String exchangeCodeForAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("redirect_uri", googleOAuthProperties.getRedirectUri());
        params.add("grant_type", "authorization_code");

        GoogleTokenResponse tokenResponse = restClient.post()
                .uri(googleOAuthProperties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(GoogleTokenResponse.class);

        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            throw new RuntimeException("Google 토큰 응답이 비어 있거나 액세스 토큰이 없습니다.");
        }
        return tokenResponse.accessToken();
    }

    private GoogleOAuthUserInfo getUserInfo(String accessToken) {
        GoogleOAuthUserInfo userInfo = restClient.get()
                .uri(googleOAuthProperties.getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GoogleOAuthUserInfo.class);
        if (userInfo == null) {
            throw new RuntimeException("Google 사용자 정보가 비어 있습니다.");
        }
        return userInfo;
    }

    public String generateGoogleAuthUrl(String state) {
        return UriComponentsBuilder.fromUriString(googleOAuthProperties.getAuthorizationUri())
                .queryParam("client_id", googleOAuthProperties.getClientId())
                .queryParam("redirect_uri", googleOAuthProperties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", googleOAuthProperties.getScope())
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .build()
                .toUriString();
    }
}
