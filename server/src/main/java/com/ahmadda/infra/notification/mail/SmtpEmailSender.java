package com.ahmadda.infra.notification.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final EmailOutboxSuccessHandler emailOutboxSuccessHandler;

    @Override
    public void sendEmails(final List<String> recipientEmails, final String subject, final String body) {
        if (recipientEmails.isEmpty()) {
            return;
        }

        MimeMessage mimeMessage = createMimeMessageWithBcc(recipientEmails, subject, body);
        javaMailSender.send(mimeMessage);
        handleSuccess(recipientEmails, subject, body);
    }

    private MimeMessage createMimeMessageWithBcc(
            final List<String> bccRecipientEmails,
            final String subject,
            final String body
    ) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("아맞다 <noreply@ahmadda.com>");
            helper.setBcc(bccRecipientEmails.toArray(String[]::new));
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
