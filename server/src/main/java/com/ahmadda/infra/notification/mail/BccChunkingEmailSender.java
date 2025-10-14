package com.ahmadda.infra.notification.mail;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BccChunkingEmailSender implements EmailSender {

    private final EmailSender delegate;
    private final int maxBcc;

    @Override
    public void sendEmails(final List<String> recipientEmails, final String subject, final String body) {
        for (int i = 0; i < recipientEmails.size(); i += maxBcc) {
            int end = Math.min(i + maxBcc, recipientEmails.size());
            List<String> chunk = recipientEmails.subList(i, end);

            delegate.sendEmails(chunk, subject, body);
        }
    }
}
