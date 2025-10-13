package com.ahmadda.infra.notification.mail;

import java.util.List;

public interface EmailOutboxNotifier {

    void sendFromOutbox(final List<String> recipientEmails, final String subject, final String body);
}
