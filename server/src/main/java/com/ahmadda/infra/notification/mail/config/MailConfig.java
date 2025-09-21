package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.BccChunkingEmailNotifier;
import com.ahmadda.infra.notification.mail.FailoverEmailNotifier;
import com.ahmadda.infra.notification.mail.GmailQuotaCircuitBreakerHandler;
import com.ahmadda.infra.notification.mail.MockEmailNotifier;
import com.ahmadda.infra.notification.mail.RetryableEmailNotifier;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
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
    public GmailQuotaCircuitBreakerHandler gmailQuotaCircuitBreakerHandler(final CircuitBreakerRegistry circuitBreakerRegistry) {
        return new GmailQuotaCircuitBreakerHandler(circuitBreakerRegistry);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier googleEmailNotifier(
            final SmtpProperties smtpProperties,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties,
            final RetryRegistry retryRegistry
    ) {
        return createEmailNotifier(
                smtpProperties.getGoogle(),
                100,
                templateEngine,
                notificationProperties,
                retryRegistry,
                "googleEmail",
                2
        );
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier awsEmailNotifier(
            final SmtpProperties smtpProperties,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties,
            final RetryRegistry retryRegistry
    ) {
        return createEmailNotifier(
                smtpProperties.getAws(),
                50,
                templateEngine,
                notificationProperties,
                retryRegistry,
                "awsEmail",
                3
        );
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
