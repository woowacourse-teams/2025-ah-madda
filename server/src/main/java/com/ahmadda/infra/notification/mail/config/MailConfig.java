package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.MockEmailNotifier;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;

@EnableConfigurationProperties({NotificationProperties.class, SmtpProperties.class})
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
            final NotificationProperties notificationProperties,
            final EntityManager em
    ) {
        return new SmtpEmailNotifier(javaMailSender, templateEngine, notificationProperties, em);
    }

    @Bean
    @Profile("prod")
    public JavaMailSender awsMailSender(final SmtpProperties smtpProperties) {
        SmtpProperties.Account account = smtpProperties.getAws();

        return getJavaMailSender(account);
    }

    @Bean
    @Profile("!prod")
    public JavaMailSender gmailMailSender(final SmtpProperties smtpProperties) {
        SmtpProperties.Account account = smtpProperties.getGoogle();

        return getJavaMailSender(account);
    }

    private JavaMailSender getJavaMailSender(final SmtpProperties.Account acc) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(acc.getHost());
        sender.setPort(acc.getPort());
        sender.setUsername(acc.getUsername());
        sender.setPassword(acc.getPassword());
        sender.setDefaultEncoding("UTF-8");
        if (acc.getProperties() != null) {
            sender.getJavaMailProperties()
                    .putAll(acc.getProperties());
        }

        return sender;
    }
}
