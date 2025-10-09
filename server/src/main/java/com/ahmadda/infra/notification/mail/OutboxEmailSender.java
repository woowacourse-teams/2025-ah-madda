package com.ahmadda.infra.notification.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class OutboxEmailSender implements EmailSender {

    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailSender delegate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void sendEmails(final List<String> recipientEmails, final String subject, final String body) {
        EmailOutbox outbox = EmailOutbox.createNow(subject, body, recipientEmails);
        emailOutboxRepository.save(outbox);

        delegate.sendEmails(recipientEmails, subject, body);
    }
}
