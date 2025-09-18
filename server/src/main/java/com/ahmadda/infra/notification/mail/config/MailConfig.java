package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.BccChunkingEmailNotifier;
import com.ahmadda.infra.notification.mail.FailoverEmailNotifier;
import com.ahmadda.infra.notification.mail.MockEmailNotifier;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    @Primary
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier failoverEmailNotifier(
            @Qualifier("googleEmailNotifier") final EmailNotifier primaryNotifier,
            @Qualifier("awsEmailNotifier") final EmailNotifier secondaryNotifier,
            final EntityManager em
    ) {
        return new FailoverEmailNotifier(primaryNotifier, secondaryNotifier, em);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier googleEmailNotifier(
            final SmtpProperties smtpProperties,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties
    ) {
        JavaMailSender googleMailSender = createJavaMailSender(smtpProperties.getGoogle());
        EmailNotifier googleEmailNotifier =
                new SmtpEmailNotifier(googleMailSender, templateEngine, notificationProperties);

        return new BccChunkingEmailNotifier(googleEmailNotifier, 100);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier awsEmailNotifier(
            final SmtpProperties smtpProperties,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties
    ) {
        JavaMailSender awsMailSender = createJavaMailSender(smtpProperties.getAws());
        EmailNotifier awsEmailNotifier =
                new SmtpEmailNotifier(awsMailSender, templateEngine, notificationProperties);

        return new BccChunkingEmailNotifier(awsEmailNotifier, 50);
    }

    private JavaMailSender createJavaMailSender(final SmtpProperties.Account acc) {
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
