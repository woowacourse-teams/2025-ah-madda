package com.ahmadda.infra.notification.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
public class EmailOutboxSuccessHandler {

    private final EmailOutboxRecipientRepository emailOutboxRecipientRepository;

    @Transactional
    public void handleSuccess(final String recipientEmail, final String subject, final String body) {
        emailOutboxRecipientRepository.deleteByRecipientEmailAndSubjectAndBody(recipientEmail, subject, body);
    }
}
