package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.OrganizationMember;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FailoverEmailNotifier implements EmailNotifier {

    private final EmailNotifier primaryNotifier;
    private final EmailNotifier secondaryNotifier;
    private final EntityManager em;

    @Override
    @Async
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "primaryEmail", fallbackMethod = "sendEmailsWithSecondary")
    public void sendEmails(
            final List<OrganizationMember> recipients,
            final EventEmailPayload payload
    ) {
        List<OrganizationMember> mergedRecipients = recipients.stream()
                .map(em::merge)
                .toList();

        primaryNotifier.sendEmails(mergedRecipients, payload);
    }

    public void sendEmailsWithSecondary(
            final List<OrganizationMember> recipients,
            final EventEmailPayload payload,
            final Throwable cause
    ) {
        log.warn(
                "failoverEmailNotifierFallback - recipients: {},  subject: {}, cause: {}",
                recipients.size(),
                payload.subject(),
                cause.getMessage(),
                cause
        );
        secondaryNotifier.sendEmails(recipients, payload);
    }
}
