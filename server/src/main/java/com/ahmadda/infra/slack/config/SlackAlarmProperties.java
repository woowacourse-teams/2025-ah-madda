package com.ahmadda.infra.slack.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
@RequiredArgsConstructor
@Getter
public class SlackAlarmProperties {

    private final String postMessageUrl;
    private final String channelId;
    private final String botToken;
    private final int connectTimeout;
    private final int readTimeout;
}
