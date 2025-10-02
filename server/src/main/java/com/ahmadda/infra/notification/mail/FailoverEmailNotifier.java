package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.ReminderEmail;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
public class FailoverEmailNotifier implements EmailNotifier {

    private final EmailNotifier primaryNotifier;
    private final EmailNotifier secondaryNotifier;

    @Override
    @Async
    @CircuitBreaker(name = "primaryEmail", fallbackMethod = "sendEmailsWithSecondary")
    public void sendEmail(final ReminderEmail reminderEmail) {
        primaryNotifier.sendEmail(reminderEmail);
    }

    public void sendEmailsWithSecondary(final ReminderEmail reminderEmail, final Throwable cause) {
        log.warn(
                "failoverEmailNotifierFallback - reminderEmail: {}, cause: {}",
                reminderEmail,
                cause.getMessage(),
                cause
        );
        secondaryNotifier.sendEmail(reminderEmail);
    }
}
