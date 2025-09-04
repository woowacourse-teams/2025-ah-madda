package com.ahmadda.infra.notification.slack.config;

import com.ahmadda.infra.notification.slack.AsyncSlackAlarm;
import com.ahmadda.infra.notification.slack.MockSlackAlarm;
import com.ahmadda.infra.notification.slack.SlackAlarm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SlackAlarmProperties.class)
public class SlackAlarmConfig {

    @Bean
    @ConditionalOnProperty(name = "slack.mock", havingValue = "true")
    public SlackAlarm mockSlackAlarm() {
        return new MockSlackAlarm();
    }

    @Bean
    @ConditionalOnProperty(name = "slack.mock", havingValue = "false", matchIfMissing = true)
    public SlackAlarm asyncSlackAlarm(
            final RestClient.Builder restClientBuilder,
            final SlackAlarmProperties slackAlarmProperties
    ) {
        return new AsyncSlackAlarm(restClientBuilder, slackAlarmProperties);
    }
}
