package com.ahmadda.infra.notification.mail;

import com.ahmadda.infra.notification.mail.exception.EmailOutboxException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
public class EmailOutboxSuccessHandler {

    private final EmailOutboxRepository emailOutboxRepository;
    private final EmailOutboxRecipientRepository emailOutboxRecipientRepository;

    @Transactional
    public void handleSuccess(final String recipientEmail, final String subject, final String body) {
        EmailOutbox outbox = emailOutboxRepository
                .findBySubjectAndBody(subject, body)
                .orElseThrow(() -> new EmailOutboxException("존재하지 않는 아웃박스입니다."));

        int deletedCount = emailOutboxRecipientRepository
                .deleteByEmailOutboxIdAndRecipientEmail(outbox.getId(), recipientEmail);
        if (deletedCount == 0) {
            throw new EmailOutboxException("존재하지 않는 아웃박스 수신자입니다.");
        }

        boolean hasRemaining = emailOutboxRecipientRepository.existsByEmailOutboxId(outbox.getId());
        if (!hasRemaining) {
            emailOutboxRepository.delete(outbox);
        }
    }
}
