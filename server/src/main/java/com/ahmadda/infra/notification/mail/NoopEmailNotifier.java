package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.ReminderEmail;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoopEmailNotifier implements EmailNotifier {

    @Override
    public void sendEmail(final ReminderEmail reminderEmail) {
        log.info("[Noop Email] reminderEmail: {}", reminderEmail);
    }
}
