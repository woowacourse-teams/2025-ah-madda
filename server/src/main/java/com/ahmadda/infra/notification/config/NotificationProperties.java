package com.ahmadda.infra.notification.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
@Getter
public class NotificationProperties {

    private final String redirectUrlPrefix;

    public NotificationProperties(final String redirectUrlPrefix) {
        validateProperties(redirectUrlPrefix);

        this.redirectUrlPrefix = redirectUrlPrefix;
    }

    private void validateProperties(final String redirectUrlPrefix) {
        if (redirectUrlPrefix == null || redirectUrlPrefix.isBlank()) {
            throw new IllegalArgumentException("Redirect Url Prefix 설정이 비어있습니다.");
        }
    }
}
