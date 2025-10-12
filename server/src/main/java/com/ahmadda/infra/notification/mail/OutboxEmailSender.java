package com.ahmadda.infra.notification.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class OutboxEmailSender implements EmailSender {

    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailOutboxRecipientRepository emailOutboxRecipientRepository;
    private final EmailSender delegate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void sendEmails(final List<String> recipientEmails, final String subject, final String body) {
        EmailOutbox outbox = EmailOutbox.createNow(subject, body);
        List<EmailOutboxRecipient> recipients = recipientEmails.stream()
                .map(email -> EmailOutboxRecipient.create(outbox, email))
                .toList();
        emailOutboxRepository.save(outbox);
        emailOutboxRecipientRepository.saveAll(recipients);

        delegate.sendEmails(recipientEmails, subject, body);
    }
}
