package com.ahmadda.domain.notification;

public interface EmailNotifier {

    void sendEmail(final ReminderEmail reminderEmail);
}
