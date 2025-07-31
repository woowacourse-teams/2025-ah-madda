package com.ahmadda.infra.mail;

import com.ahmadda.domain.Email;
import com.ahmadda.domain.NotificationMailer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockNotificationMailer implements NotificationMailer {

    @Override
    public void sendEmail(
            final String recipientEmail,
            final Email email
    ) {
        log.info("[Mock Email] To: {} | Subject: {} | Body: {}", recipientEmail, email.subject(), email.body());
    }
}
