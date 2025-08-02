package com.ahmadda.infra.slack.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
@Getter
public class SlackAlarmProperties {

    private final String postMessageUrl;
    private final String channelId;
    private final String botToken;
    private final int connectTimeout;
    private final int readTimeout;

    public SlackAlarmProperties(
            final String postMessageUrl,
            final String channelId,
            final String botToken,
            final int connectTimeout,
            final int readTimeout
    ) {
        validateProperties(postMessageUrl, channelId, botToken, connectTimeout, readTimeout);

        this.postMessageUrl = postMessageUrl;
        this.channelId = channelId;
        this.botToken = botToken;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    private void validateProperties(
            final String postMessageUrl,
            final String channelId,
            final String botToken,
            final int connectTimeout,
            final int readTimeout
    ) {
        if (postMessageUrl == null || postMessageUrl.isBlank()) {
            throw new IllegalArgumentException("Slack postMessageUrl이 비어있습니다.");
        }
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("Slack channelId가 비어있습니다.");
        }
        if (botToken == null || botToken.isBlank()) {
            throw new IllegalArgumentException("Slack botToken이 비어있습니다.");
        }
        if (connectTimeout <= 0) {
            throw new IllegalArgumentException("Slack connectTimeout은 0보다 커야 합니다.");
        }
        if (readTimeout <= 0) {
            throw new IllegalArgumentException("Slack readTimeout은 0보다 커야 합니다.");
        }
    }
}
