package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.notification.ReminderEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@RequiredArgsConstructor
public class BccChunkingEmailNotifier implements EmailNotifier, EmailOutboxNotifier {

    private final EmailNotifier delegate;
    private final int maxBcc;

    @Override
    public void remind(final ReminderEmail reminderEmail) {
        List<String> recipientEmails = reminderEmail.recipientEmails();
        EventEmailPayload payload = reminderEmail.payload();

        for (int i = 0; i < recipientEmails.size(); i += maxBcc) {
            int end = Math.min(i + maxBcc, recipientEmails.size());
            List<String> chunk = recipientEmails.subList(i, end);

            delegate.remind(new ReminderEmail(chunk, payload));
        }
    }

    @Override
    @Async
    public void sendFromOutbox(
            final List<String> recipientEmails,
            final String subject,
            final String body
    ) {
        for (int i = 0; i < recipientEmails.size(); i += maxBcc) {
            int end = Math.min(i + maxBcc, recipientEmails.size());
            List<String> chunk = recipientEmails.subList(i, end);

            // TODO. 추후 ISP를 준수하도록 변경
            if (delegate instanceof EmailOutboxNotifier notifier) {
                notifier.sendFromOutbox(chunk, subject, body);
            }
        }
    }
}
