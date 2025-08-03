package com.ahmadda.infra.push.config;

import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.push.FcmPushErrorHandler;
import com.ahmadda.infra.push.FcmPushNotifier;
import com.ahmadda.infra.push.FcmPushTokenRepository;
import com.ahmadda.infra.push.MockPushNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushConfig {

    @Bean
    @ConditionalOnProperty(name = "push.mock", havingValue = "true")
    public PushNotifier mockPushNotifier() {
        return new MockPushNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "push.mock", havingValue = "false", matchIfMissing = true)
    public PushNotifier fcmPushNotifier(final FcmPushTokenRepository fcmPushTokenRepository) {
        return new FcmPushNotifier(new FcmPushErrorHandler(fcmPushTokenRepository));
    }
}
