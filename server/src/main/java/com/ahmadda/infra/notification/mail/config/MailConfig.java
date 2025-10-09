package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.BccChunkingEmailSender;
import com.ahmadda.infra.notification.mail.EmailOutboxRepository;
import com.ahmadda.infra.notification.mail.EmailOutboxSuccessHandler;
import com.ahmadda.infra.notification.mail.EmailOutboxRecipientRepository;
import com.ahmadda.infra.notification.mail.EmailOutboxRepository;
import com.ahmadda.infra.notification.mail.EmailOutboxScheduler;
import com.ahmadda.infra.notification.mail.EmailOutboxSuccessHandler;
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

@EnableConfigurationProperties({NotificationProperties.class, SmtpProperties.class})
@Configuration
public class MailConfig {

    @Bean
    public EmailSender outboxEmailSender(
            final EmailOutboxRepository emailOutboxRepository,
            final EmailOutboxRecipientRepository emailOutboxRecipientRepository,
            @Qualifier("failoverEmailNotifier") final EmailNotifier failoverEmailNotifier
    ) {
        return new OutboxEmailNotifier(
                templateEngine,
                notificationProperties,
                emailOutboxRepository,
                emailOutboxRecipientRepository,
                failoverEmailNotifier
        );
    }

    @Bean
            final EmailOutboxRepository emailOutboxRepository,
            @Qualifier("failoverEmailNotifier") final EmailNotifier failoverEmailNotifier
            @Qualifier("failoverEmailSender") final EmailSender failoverEmailSender
    ) {
        return new OutboxEmailSender(emailOutboxRepository, failoverEmailSender);
    }

    @Bean
    public EmailSender failoverEmailSender(
            final RetryRegistry retryRegistry,
            final EmailSender googleSmtpEmailSender,
            final EmailSender awsSmtpEmailSender
    ) {
        EmailSender googleRetryable =
                new RetryableEmailSender(googleSmtpEmailSender, retryRegistry, "googleEmail", 2, 1000);
        EmailSender awsRetryable =
                new RetryableEmailSender(awsSmtpEmailSender, retryRegistry, "awsEmail", 3, 1000);

        EmailSender googleChunked = new BccChunkingEmailSender(googleRetryable, 100);
        EmailSender awsChunked = new BccChunkingEmailSender(awsRetryable, 50);

        return new FailoverEmailSender(googleChunked, awsChunked);
    }

    @Bean
    public GmailQuotaCircuitBreakerHandler gmailQuotaCircuitBreakerHandler(final CircuitBreakerRegistry circuitBreakerRegistry) {
        return new GmailQuotaCircuitBreakerHandler(circuitBreakerRegistry);
    }

    @Bean
    public EmailOutboxScheduler emailOutboxScheduler(
            final EmailOutboxRepository emailOutboxRepository,
            final EmailOutboxRecipientRepository emailOutboxRecipientRepository,
            final EmailOutboxNotifier emailOutboxNotifier
    ) {
        return new EmailOutboxScheduler(emailOutboxRepository, emailOutboxRecipientRepository, emailOutboxNotifier);
            final EmailOutboxNotifier emailOutboxNotifier
    ) {
        return new EmailOutboxScheduler(emailOutboxRepository, emailOutboxNotifier);
    }

    @Bean
    public EmailOutboxNotifier awsOutboxNotifier(
    public EmailSender googleSmtpEmailSender(
            final SmtpProperties smtpProperties,
            final EmailOutboxSuccessHandler emailOutboxSuccessHandler
    ) {
        JavaMailSender sender = createJavaMailSender(smtpProperties.getGoogle());
        return new SmtpEmailSender(sender, emailOutboxSuccessHandler);
    }

    @Bean
    public EmailOutboxSuccessHandler smtpEmailSuccessHandler(
            final EmailOutboxRepository emailOutboxRepository,
            final EmailOutboxRecipientRepository emailOutboxRecipientRepository
    ) {
        return new EmailOutboxSuccessHandler(emailOutboxRepository, emailOutboxRecipientRepository);
    public EmailOutboxSuccessHandler smtpEmailSuccessHandler(final EmailOutboxRecipientRepository emailOutboxRecipientRepository) {
        return new EmailOutboxSuccessHandler(emailOutboxRecipientRepository);
    public EmailSender awsSmtpEmailSender(
            final SmtpProperties smtpProperties,
            final EmailOutboxSuccessHandler emailOutboxSuccessHandler
    ) {
        JavaMailSender sender = createJavaMailSender(smtpProperties.getAws());
        return new SmtpEmailSender(sender, emailOutboxSuccessHandler);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "mail.noop", havingValue = "true")
    public EmailSender noopEmailSender() {
        return new NoopEmailSender();
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
