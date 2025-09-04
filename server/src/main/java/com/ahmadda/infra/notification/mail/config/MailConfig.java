package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.MockEmailNotifier;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@EnableConfigurationProperties(NotificationProperties.class)
@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "true")
    public EmailNotifier mockEmailNotifier() {
        return new MockEmailNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier smtpEmailNotifier(
            final JavaMailSender javaMailSender,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties
    ) {
        return new SmtpEmailNotifier(javaMailSender, templateEngine, notificationProperties);
    }
}
