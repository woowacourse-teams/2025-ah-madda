package com.ahmadda.domain;

public interface NotificationMailer {

    void sendNotification(final String recipientEmail, final String subject, final String content);
}
