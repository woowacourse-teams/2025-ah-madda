package com.ahmadda.infra.mail;

import com.ahmadda.domain.NotificationMailer;
import com.ahmadda.infra.mail.exception.MailSendFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
public class SmtpNotificationMailer implements NotificationMailer {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendEmail(final String recipientEmail, final String subject, final String text) {
        MimeMessage mimeMessage = createMimeMessage(recipientEmail, subject, text);

        javaMailSender.send(mimeMessage);
    }

    private MimeMessage createMimeMessage(final String recipientEmail, final String subject, final String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            log.error("mailError : {} ", e.getMessage(), e);
            throw new MailSendFailedException("이메일 발송에 실패했습니다.", e);
        }

        return mimeMessage;
    }
}
