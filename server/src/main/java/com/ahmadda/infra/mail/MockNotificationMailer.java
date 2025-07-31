package com.ahmadda.infra.mail;

import com.ahmadda.domain.NotificationMailer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MockNotificationMailer implements NotificationMailer {

    @Override
    public void sendEmail(
            final String recipientEmail,
            final String subject,
            final Map<String, Object> model
    ) {
        log.info("[Mock Email] To: {} | Subject: {} | Model: {}", recipientEmail, subject, model);
    }
}
