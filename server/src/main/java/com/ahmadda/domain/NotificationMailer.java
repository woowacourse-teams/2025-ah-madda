package com.ahmadda.domain;

public interface NotificationMailer {

    void sendEmail(final String recipientEmail, final EventEmailPayload eventEmailPayload);
}
