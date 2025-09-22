package com.ahmadda.infra.notification.push.config;

import com.ahmadda.domain.notification.PushNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.push.FcmPushErrorHandler;
import com.ahmadda.infra.notification.push.FcmPushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import com.ahmadda.infra.notification.push.NoopPushNotifier;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(NotificationProperties.class)
@Configuration
public class PushConfig {

    @Bean
    @ConditionalOnProperty(name = "push.noob", havingValue = "true")
    public PushNotifier noobPushNotifier() {
        return new NoopPushNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "push.noob", havingValue = "false", matchIfMissing = true)
    public PushNotifier fcmPushNotifier(
            final FcmRegistrationTokenRepository fcmRegistrationTokenRepository,
            final NotificationProperties notificationProperties,
            final EntityManager em
    ) {
        return new FcmPushNotifier(
                fcmRegistrationTokenRepository,
                new FcmPushErrorHandler(fcmRegistrationTokenRepository),
                notificationProperties,
                em
        );
    }
}
