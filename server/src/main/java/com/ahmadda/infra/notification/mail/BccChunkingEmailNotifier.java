package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.notification.ReminderEmail;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BccChunkingEmailNotifier implements EmailNotifier {

    private final EmailNotifier delegate;
    private final int maxBcc;

    @Override
    public void sendEmail(final ReminderEmail reminderEmail) {
        List<String> recipientEmails = reminderEmail.recipientEmails();
        EventEmailPayload payload = reminderEmail.payload();

        for (int i = 0; i < recipientEmails.size(); i += maxBcc) {
            int end = Math.min(i + maxBcc, recipientEmails.size());
            List<String> chunk = recipientEmails.subList(i, end);

            delegate.sendEmail(new ReminderEmail(chunk, payload));
        }
    }
}
