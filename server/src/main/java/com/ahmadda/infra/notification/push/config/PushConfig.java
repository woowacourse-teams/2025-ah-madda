package com.ahmadda.infra.notification.push.config;

import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.push.FcmPushErrorHandler;
import com.ahmadda.infra.notification.push.FcmPushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import com.ahmadda.infra.notification.push.MockPushNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(NotificationProperties.class)
@Configuration
public class PushConfig {

    @Bean
    @ConditionalOnProperty(name = "push.mock", havingValue = "true")
    public PushNotifier mockPushNotifier() {
        return new MockPushNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "push.mock", havingValue = "false", matchIfMissing = true)
    public PushNotifier fcmPushNotifier(
            final FcmRegistrationTokenRepository fcmRegistrationTokenRepository,
            final NotificationProperties notificationProperties
    ) {
        return new FcmPushNotifier(
                fcmRegistrationTokenRepository,
                new FcmPushErrorHandler(fcmRegistrationTokenRepository),
                notificationProperties
        );
    }
}
