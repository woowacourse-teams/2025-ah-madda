package com.ahmadda.infra;


import com.ahmadda.domain.util.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class GoogleOAuthProvider {

    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;
    private final String userUri;
    private final RestClient restClient;

    public GoogleOAuthProvider(
            @Value("${google.oauth2.client-id}") String clientId,
            @Value("${google.oauth2.client-secret}") String clientSecret,
            @Value("${google.oauth2.redirect-uri}") String redirectUri,
            @Value("${google.oauth2.token-uri}") String tokenUri,
            @Value("${google.oauth2.user-info-uri}") String userUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
        this.restClient = RestClient.builder().build();
    }

    public OAuthUserInfoResponse getUserInfo(final String code) {
        String googleAccessToken = requestGoogleAccessToken(code);

        return requestGoogleUserInfo(googleAccessToken);
    }

    private String requestGoogleAccessToken(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", GRANT_TYPE_AUTHORIZATION_CODE);

        GoogleAccessTokenResponse googleAccessTokenResponse = restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(GoogleAccessTokenResponse.class);

        Assert.notNull(googleAccessTokenResponse.accessToken(), "accessToken null이 되면 안됩니다.");

        return googleAccessTokenResponse.accessToken();
    }

    private OAuthUserInfoResponse requestGoogleUserInfo(final String accessToken) {
        OAuthUserInfoResponse userInfo = restClient.get()
                .uri(userUri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(OAuthUserInfoResponse.class);

        Assert.notNull(userInfo, "userInfo null이 되면 안됩니다.");

        return userInfo;
    }
}
