package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.ReminderEmail;
import com.ahmadda.infra.notification.config.NotificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEmailNotifier implements EmailNotifier {

    private final TemplateEngine templateEngine;
    private final NotificationProperties notificationProperties;
    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailOutboxRecipientRepository emailOutboxRecipientRepository;
    private final EmailNotifier delegate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remind(final ReminderEmail reminderEmail) {
        String subject = reminderEmail.payload()
                .renderSubject();
        String body = reminderEmail.payload()
                .renderBody(templateEngine, notificationProperties.getRedirectUrlPrefix());
        List<String> recipientEmails = reminderEmail.recipientEmails();

        EmailOutbox outbox = EmailOutbox.createNow(subject, body);
        List<EmailOutboxRecipient> recipients = recipientEmails.stream()
                .map(email -> EmailOutboxRecipient.create(outbox, email))
                .toList();
        emailOutboxRepository.save(outbox);
        emailOutboxRecipientRepository.saveAll(recipients);

        delegate.remind(reminderEmail);
    }
}
