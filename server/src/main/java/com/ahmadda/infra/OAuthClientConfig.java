package com.ahmadda.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OAuthClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
