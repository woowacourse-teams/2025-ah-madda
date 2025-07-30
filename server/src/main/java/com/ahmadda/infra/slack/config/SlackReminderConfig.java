package com.ahmadda.infra.slack.config;

import com.ahmadda.infra.slack.AsyncSlackReminder;
import com.ahmadda.infra.slack.MockProductionSlackReminder;
import com.ahmadda.infra.slack.SlackReminder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class SlackReminderConfig {

    @Bean
    @Profile("prod")
    public SlackReminder slackReminder(final RestClient.Builder restClientBuilder,
                                       final SlackAlarmProperties slackAlarmProperties) {

        restClientBuilder.requestFactory(simpleClientHttpRequestFactory(slackAlarmProperties));
        restClientBuilder.baseUrl(slackAlarmProperties.getPostMessageUrl());
        restClientBuilder.defaultHeaders(headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(slackAlarmProperties.getBotToken());
        });
        RestClient restClient = restClientBuilder.build();

        return new AsyncSlackReminder(restClient, slackAlarmProperties);
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(final SlackAlarmProperties slackAlarmProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(slackAlarmProperties.getConnectTimeout());
        factory.setReadTimeout(slackAlarmProperties.getReadTimeout());

        return factory;
    }

    @Bean
    @Profile("!prod")
    public SlackReminder mockSlackReminder() {
        return new MockProductionSlackReminder();
    }
}
