package com.ahmadda.infra.auth.oauth;


import com.ahmadda.infra.auth.oauth.config.GoogleOAuthProperties;
import com.ahmadda.infra.auth.oauth.dto.GoogleAccessTokenResponse;
import com.ahmadda.infra.auth.oauth.dto.OAuthUserInfoResponse;
import com.ahmadda.infra.auth.oauth.exception.InvalidOauthTokenException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@EnableConfigurationProperties(GoogleOAuthProperties.class)
public class GoogleOAuthProvider {

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private final GoogleOAuthProperties googleOAuthProperties;
    private final RestClient restClient;

    public GoogleOAuthProvider(
            final GoogleOAuthProperties googleOAuthProperties,
            final RestClient.Builder restClientBuilder
    ) {
        this.googleOAuthProperties = googleOAuthProperties;
        this.restClient = restClientBuilder
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    public OAuthUserInfoResponse getUserInfo(final String code, final String redirectUri) {
        String googleAccessToken = requestGoogleAccessToken(code, redirectUri);

        return requestGoogleUserInfo(googleAccessToken);
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(googleOAuthProperties.getConnectTimeout());
        factory.setReadTimeout(googleOAuthProperties.getReadTimeout());

        return factory;
    }

    private String requestGoogleAccessToken(final String code, final String redirectUri) {
        MultiValueMap<String, String> tokenRequestParams = createTokenRequestParams(code, redirectUri);

        GoogleAccessTokenResponse googleAccessTokenResponse = restClient.post()
                .uri(googleOAuthProperties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(tokenRequestParams)
                .retrieve()
                .body(GoogleAccessTokenResponse.class);

        if (googleAccessTokenResponse == null) {
            throw new InvalidOauthTokenException("유효하지 않는 인증 정보입니다. 인가 코드가 만료되었거나, 잘못되었습니다.");
        }

        return googleAccessTokenResponse.accessToken();
    }

    private OAuthUserInfoResponse requestGoogleUserInfo(final String accessToken) {
        return restClient.get()
                .uri(googleOAuthProperties.getUserUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(OAuthUserInfoResponse.class);
    }

    private MultiValueMap<String, String> createTokenRequestParams(final String code, final String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", GRANT_TYPE_AUTHORIZATION_CODE);

        return params;
    }
}
