package com.ahmadda.infra.notification.slack.config;

import com.ahmadda.infra.notification.slack.AsyncSlackAlarm;
import com.ahmadda.infra.notification.slack.NoopSlackAlarm;
import com.ahmadda.infra.notification.slack.SlackAlarm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SlackAlarmProperties.class)
public class SlackAlarmConfig {

    @Bean
    public SlackAlarm asyncSlackAlarm(
            final RestClient.Builder restClientBuilder,
            final SlackAlarmProperties slackAlarmProperties
    ) {
        return new AsyncSlackAlarm(restClientBuilder, slackAlarmProperties);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "slack.noop", havingValue = "true")
    public SlackAlarm noopSlackAlarm() {
        return new NoopSlackAlarm();
    }
}
