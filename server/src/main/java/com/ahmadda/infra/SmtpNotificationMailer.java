package com.ahmadda.infra;

import com.ahmadda.domain.NotificationMailer;
import com.ahmadda.infra.exception.MailSendFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class SmtpNotificationMailer implements NotificationMailer {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendNotification(final String recipientEmail, final String subject, final String content) {
        MimeMessage mimeMessage = createMimeMessage(recipientEmail, subject, content);

        javaMailSender.send(mimeMessage);
    }

    private MimeMessage createMimeMessage(final String recipientEmail, final String subject, final String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new MailSendFailedException("이메일 발송에 실패했습니다.", e);
        }

        return mimeMessage;
    }
}
