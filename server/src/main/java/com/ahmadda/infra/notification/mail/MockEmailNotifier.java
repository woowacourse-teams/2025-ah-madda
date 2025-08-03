package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.EventEmailPayload;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MockEmailNotifier implements EmailNotifier {

    @Override
    public void sendEmails(
            final List<String> recipientEmails,
            final EventEmailPayload eventEmailPayload
    ) {
        log.info(
                "[Mock Email] To: {} | Subject: {} | Body: {}",
                recipientEmails,
                eventEmailPayload.subject(),
                eventEmailPayload.body()
        );
    }
}
