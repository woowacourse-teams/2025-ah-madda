package com.ahmadda.infra.notification.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class EmailOutboxScheduler {

    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailOutboxNotifier emailOutboxNotifier;

    private static final int LOCK_TTL_MINUTES = 5;

    @Transactional
    @Scheduled(fixedDelay = 60_000)
    public void resendFailedEmails() {
        LocalDateTime threshold = LocalDateTime.now()
                .minusMinutes(LOCK_TTL_MINUTES);
        List<EmailOutbox> failedOutboxes = emailOutboxRepository.findAndLockExpiredOutboxes(threshold);

        for (EmailOutbox outbox : failedOutboxes) {
            List<String> recipients = outbox.getRecipients()
                    .stream()
                    .map(EmailOutboxRecipient::getRecipientEmail)
                    .toList();

            if (recipients.isEmpty()) {
                emailOutboxRepository.delete(outbox);
                continue;
            }

            outbox.lock();
            emailOutboxNotifier.sendFromOutbox(recipients, outbox.getSubject(), outbox.getBody());
        }
    }
}
