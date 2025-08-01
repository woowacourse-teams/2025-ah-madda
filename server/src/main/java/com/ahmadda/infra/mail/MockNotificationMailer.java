package com.ahmadda.infra.mail;

import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.NotificationMailer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockNotificationMailer implements NotificationMailer {

    @Override
    public void sendEmail(
            final String recipientEmail,
            final EventEmailPayload eventEmailPayload
    ) {
        log.info(
                "[Mock Email] To: {} | Subject: {} | Body: {}",
                recipientEmail,
                eventEmailPayload.subject(),
                eventEmailPayload.body()
        );
    }
}
