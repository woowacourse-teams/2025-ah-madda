package com.ahmadda.infra.config;

import com.ahmadda.domain.NotificationMailer;
import com.ahmadda.infra.mail.MockNotificationMailer;
import com.ahmadda.infra.mail.SmtpNotificationMailer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "true")
    public NotificationMailer mockMailService() {
        return new MockNotificationMailer();
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public NotificationMailer mailServiceImpl(final JavaMailSender javaMailSender) {
        return new SmtpNotificationMailer(javaMailSender);
    }
}
