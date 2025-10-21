package com.ahmadda.infra.notification.mail;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NoopEmailSender implements EmailSender {

    @Override
    public void sendEmails(final List<String> recipientEmails, final String subject, final String body) {
        log.info("[Noop Email] recipientEmails: {}, subject: {}, body: {}", recipientEmails, subject, body);
    }
}
