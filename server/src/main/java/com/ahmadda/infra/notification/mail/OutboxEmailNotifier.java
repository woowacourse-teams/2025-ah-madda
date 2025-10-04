package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.ReminderEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxEmailNotifier implements EmailNotifier {

    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailNotifier delegate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remind(final ReminderEmail reminderEmail) {
        Long eventId = reminderEmail.payload()
                .body()
                .eventId();
        String content = reminderEmail.payload()
                .body()
                .content();

        for (String recipientEmail : reminderEmail.recipientEmails()) {
            EmailOutbox outbox = EmailOutbox.createNow(eventId, recipientEmail, content);
            emailOutboxRepository.save(outbox);
        }

        delegate.remind(reminderEmail);
    }
}
