package com.ahmadda.infra.notification.mail;

import java.util.List;

public interface EmailSender {

    void sendEmails(final List<String> recipientEmails, final String subject, final String body);
}
