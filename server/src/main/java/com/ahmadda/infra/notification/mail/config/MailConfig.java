package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.BccChunkingEmailNotifier;
import com.ahmadda.infra.notification.mail.EmailOutboxRepository;
import com.ahmadda.infra.notification.mail.FailoverEmailNotifier;
import com.ahmadda.infra.notification.mail.GmailQuotaCircuitBreakerHandler;
import com.ahmadda.infra.notification.mail.NoopEmailNotifier;
import com.ahmadda.infra.notification.mail.OutboxEmailNotifier;
import com.ahmadda.infra.notification.mail.RetryableEmailNotifier;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
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
    @Primary
    @ConditionalOnProperty(name = "mail.noop", havingValue = "false", matchIfMissing = true)
    public EmailNotifier outboxEmailNotifier(
            final EmailOutboxRepository emailOutboxRepository,
            @Qualifier("failoverEmailNotifier") final EmailNotifier failoverEmailNotifier
    ) {
        return new OutboxEmailNotifier(emailOutboxRepository, failoverEmailNotifier);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.noop", havingValue = "false", matchIfMissing = true)
    public EmailNotifier failoverEmailNotifier(
            final SmtpProperties smtpProperties,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties,
            final RetryRegistry retryRegistry
    ) {
        EmailNotifier googleEmailNotifier = createEmailNotifier(
                smtpProperties.getGoogle(),
                100,
                templateEngine,
                notificationProperties,
                retryRegistry,
                "googleEmail",
                2
        );

        EmailNotifier awsEmailNotifier = createEmailNotifier(
                smtpProperties.getAws(),
                50,
                templateEngine,
                notificationProperties,
                retryRegistry,
                "awsEmail",
                3
        );

        return new FailoverEmailNotifier(googleEmailNotifier, awsEmailNotifier);
    }

    @Bean
    public GmailQuotaCircuitBreakerHandler gmailQuotaCircuitBreakerHandler(final CircuitBreakerRegistry circuitBreakerRegistry) {
        return new GmailQuotaCircuitBreakerHandler(circuitBreakerRegistry);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "mail.noop", havingValue = "true")
    public EmailNotifier noopEmailNotifier() {
        return new NoopEmailNotifier();
    }

    private EmailNotifier createEmailNotifier(
            final SmtpProperties.Account account,
            final int maxBcc,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties,
            final RetryRegistry retryRegistry,
            final String retryName,
            final int maxAttempts
    ) {
        JavaMailSender sender = createJavaMailSender(account);
        SmtpEmailNotifier smtp = new SmtpEmailNotifier(sender, templateEngine, notificationProperties);
        RetryableEmailNotifier retryable =
                new RetryableEmailNotifier(retryRegistry, retryName, smtp, maxAttempts, 1000);
        return new BccChunkingEmailNotifier(retryable, maxBcc);
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
