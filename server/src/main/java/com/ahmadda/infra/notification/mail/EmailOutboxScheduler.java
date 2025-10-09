package com.ahmadda.infra.notification.mail;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EmailOutboxScheduler {

    private final EmailSender emailSender;
    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailOutboxRecipientRepository emailOutboxRecipientRepository;
    private final EmailOutboxNotifier emailOutboxNotifier;

    private static final int SOFT_LOCK_TTL_MINUTES = 5;

    public EmailOutboxScheduler(
            @Qualifier("failoverEmailSender") final EmailSender emailSender,
            final EmailOutboxRepository emailOutboxRepository
    ) {
        this.emailSender = emailSender;
        this.emailOutboxRepository = emailOutboxRepository;
    }

    @Transactional
    @Scheduled(fixedDelay = 60_000)
    public void resendFailedEmails() {
        LocalDateTime threshold = LocalDateTime.now()
                .minusMinutes(SOFT_LOCK_TTL_MINUTES);
        List<EmailOutbox> failedOutboxes = emailOutboxRepository.findAndLockExpiredOutboxes(threshold);

        for (EmailOutbox outbox : failedOutboxes) {
            List<EmailOutboxRecipient> recipients =
                    emailOutboxRecipientRepository.findAllByEmailOutboxId(outbox.getId());

            if (recipients.isEmpty()) {
                emailOutboxRepository.delete(outbox);
                continue;
            }

            outbox.lock();

            List<String> recipientEmails = recipients.stream()
                    .map(EmailOutboxRecipient::getRecipientEmail)
                    .toList();
            emailOutboxNotifier.sendFromOutbox(recipientEmails, outbox.getSubject(), outbox.getBody());
        }
    }
}
