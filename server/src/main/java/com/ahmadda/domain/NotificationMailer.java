package com.ahmadda.domain;

public interface NotificationMailer {

    void sendEmail(final String recipientEmail, final String subject, final String content);
}
