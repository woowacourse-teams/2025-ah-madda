package com.ahmadda.infra;


import com.ahmadda.domain.util.Assert;
import com.ahmadda.infra.dto.GoogleAccessTokenResponse;
import com.ahmadda.infra.dto.OAuthUserInfoResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@EnableConfigurationProperties(GoogleOAuthProperties.class)
public class GoogleOAuthProvider {

    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private final GoogleOAuthProperties googleOAuthProperties;
    private final RestClient restClient;

    public GoogleOAuthProvider(
            final GoogleOAuthProperties googleOAuthProperties,
            final RestClient.Builder restClientBuilder
    ) {
        this.googleOAuthProperties = googleOAuthProperties;
        this.restClient = restClientBuilder
                .requestFactory(bufferingClientHttpRequestFactory())
                .build();
    }

    private BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(googleOAuthProperties.getConnectTimeout());
        factory.setReadTimeout(googleOAuthProperties.getReadTimeout());

        return new BufferingClientHttpRequestFactory(factory);
    }

    public OAuthUserInfoResponse getUserInfo(final String code) {
        String googleAccessToken = requestGoogleAccessToken(code);

        return requestGoogleUserInfo(googleAccessToken);
    }

    private String requestGoogleAccessToken(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("redirect_uri", googleOAuthProperties.getRedirectUri());
        params.add("grant_type", GRANT_TYPE_AUTHORIZATION_CODE);

        GoogleAccessTokenResponse googleAccessTokenResponse = restClient.post()
                .uri(googleOAuthProperties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(GoogleAccessTokenResponse.class);

        Assert.notNull(googleAccessTokenResponse.accessToken(), "accessToken null이 되면 안됩니다.");

        return googleAccessTokenResponse.accessToken();
    }

    private OAuthUserInfoResponse requestGoogleUserInfo(final String accessToken) {
        OAuthUserInfoResponse userInfo = restClient.get()
                .uri(googleOAuthProperties.getUserUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(OAuthUserInfoResponse.class);

        Assert.notNull(userInfo, "userInfo null이 되면 안됩니다.");

        return userInfo;
    }
}
