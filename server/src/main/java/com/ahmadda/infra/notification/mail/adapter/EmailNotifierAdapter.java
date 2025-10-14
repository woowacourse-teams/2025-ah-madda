package com.ahmadda.infra.notification.mail.adapter;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.ReminderEmail;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.EmailSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Component
public class EmailNotifierAdapter implements EmailNotifier {

    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;
    private final NotificationProperties notificationProperties;

    public EmailNotifierAdapter(
            @Qualifier("outboxEmailSender") final EmailSender emailSender,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties
    ) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.notificationProperties = notificationProperties;
    }

    @Override
    public void remind(final ReminderEmail reminderEmail) {
        List<String> recipientEmails = reminderEmail.recipientEmails();
        String subject = reminderEmail.payload()
                .renderSubject();
        String body = reminderEmail.payload()
                .renderBody(templateEngine, notificationProperties.getRedirectUrlPrefix());

        emailSender.sendEmails(recipientEmails, subject, body);
    }
}
