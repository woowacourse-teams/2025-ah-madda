package com.ahmadda.infra.notification.push.config;

import com.ahmadda.domain.notification.PushNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.push.FcmPushErrorHandler;
import com.ahmadda.infra.notification.push.FcmPushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import com.ahmadda.infra.notification.push.NoopPushNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableConfigurationProperties(NotificationProperties.class)
@Configuration
public class PushConfig {

    @Bean
    public PushNotifier fcmPushNotifier(
            final FcmRegistrationTokenRepository fcmRegistrationTokenRepository,
            final FcmPushErrorHandler fcmPushErrorHandler,
            final NotificationProperties notificationProperties
    ) {
        return new FcmPushNotifier(
                fcmRegistrationTokenRepository,
                fcmPushErrorHandler,
                notificationProperties
        );
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "push.noop", havingValue = "true")
    public PushNotifier noopPushNotifier() {
        return new NoopPushNotifier();
    }
}
