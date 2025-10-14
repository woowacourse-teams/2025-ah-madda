package com.ahmadda.infra.notification.mail;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FailoverEmailSender implements EmailSender {

    private final EmailSender primaryEmailSender;
    private final EmailSender secondaryEmailSender;

    @Override
    @Async
    @CircuitBreaker(name = "primaryEmail", fallbackMethod = "sendEmailsWithSecondary")
    public void sendEmails(final List<String> recipientEmails, final String subject, final String body) {
        primaryEmailSender.sendEmails(recipientEmails, subject, body);
    }

    public void sendEmailsWithSecondary(
            final List<String> recipientEmails,
            final String subject,
            final String body,
            final Throwable cause
    ) {
        log.warn(
                "failoverEmailNotifierFallback - recipientEmails: {}, cause: {}",
                recipientEmails,
                cause.getMessage(),
                cause
        );
        secondaryEmailSender.sendEmails(recipientEmails, subject, body);
    }
}
