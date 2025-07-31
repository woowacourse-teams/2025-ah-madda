package com.ahmadda.domain;

import java.util.Map;

public interface NotificationMailer {

    void sendEmail(final String recipientEmail, final String subject, final Map<String, Object> model);
}
