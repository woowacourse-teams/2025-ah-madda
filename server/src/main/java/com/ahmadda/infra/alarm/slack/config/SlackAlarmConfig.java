package com.ahmadda.infra.alarm.slack.config;

import com.ahmadda.infra.alarm.slack.AsyncSlackAlarm;
import com.ahmadda.infra.alarm.slack.MockSlackAlarm;
import com.ahmadda.infra.alarm.slack.SlackAlarm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SlackAlarmProperties.class)
public class SlackAlarmConfig {

    @Bean
    @ConditionalOnProperty(name = "slack.mock", havingValue = "true")
    public SlackAlarm mockSlackReminder() {
        return new MockSlackAlarm();
    }

    @Bean
    @ConditionalOnProperty(name = "slack.mock", havingValue = "false", matchIfMissing = true)
    public SlackAlarm slackReminder(
            final RestClient.Builder restClientBuilder,
            final SlackAlarmProperties slackAlarmProperties
    ) {

        restClientBuilder.requestFactory(simpleClientHttpRequestFactory(slackAlarmProperties));
        restClientBuilder.baseUrl(slackAlarmProperties.getPostMessageUrl());
        restClientBuilder.defaultHeaders(headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(slackAlarmProperties.getBotToken());
        });
        RestClient restClient = restClientBuilder.build();

        return new AsyncSlackAlarm(restClient, slackAlarmProperties);
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(final SlackAlarmProperties slackAlarmProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(slackAlarmProperties.getConnectTimeout());
        factory.setReadTimeout(slackAlarmProperties.getReadTimeout());

        return factory;
    }
}
