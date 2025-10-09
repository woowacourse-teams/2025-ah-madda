package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.notification.ReminderEmail;
import com.ahmadda.infra.notification.config.NotificationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SmtpEmailNotifier implements EmailNotifier {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final NotificationProperties notificationProperties;
    private final EmailOutboxSuccessHandler emailOutboxSuccessHandler;

    @Override
    public void remind(final ReminderEmail reminderEmail) {
        List<String> recipientEmails = reminderEmail.recipientEmails();
        EventEmailPayload eventEmailPayload = reminderEmail.payload();

        if (recipientEmails.isEmpty()) {
            return;
        }

        String subject = eventEmailPayload.renderSubject();
        String body = eventEmailPayload.renderBody(templateEngine, notificationProperties.getRedirectUrlPrefix());

        MimeMessage mimeMessage = createMimeMessageWithBcc(recipientEmails, subject, body);
        javaMailSender.send(mimeMessage);
        handleSuccess(recipientEmails, subject, body);
    }

    private MimeMessage createMimeMessageWithBcc(
            final List<String> bccRecipients,
            final String subject,
            final String body
    ) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("아맞다 <noreply@ahmadda.com>");
            helper.setBcc(bccRecipients.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(body, true);
        } catch (MessagingException e) {
            log.error("mailError : {} ", e.getMessage(), e);
        }

        return mimeMessage;
    }


    private void handleSuccess(final List<String> recipientEmails, final String subject, final String body) {
        for (String recipientEmail : recipientEmails) {
            emailOutboxSuccessHandler.handleSuccess(recipientEmail, subject, body);
        }
    }
}
