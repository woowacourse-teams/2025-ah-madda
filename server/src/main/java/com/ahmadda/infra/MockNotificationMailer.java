package com.ahmadda.infra;

import com.ahmadda.domain.NotificationMailer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockNotificationMailer implements NotificationMailer {

    @Override
    public void sendNotification(
            final String recipientEmail,
            final String subject,
            final String content
    ) {
        log.info("[Mock Email] To: {} | Subject: {} | Content: {}", recipientEmail, subject, content);
    }
}
