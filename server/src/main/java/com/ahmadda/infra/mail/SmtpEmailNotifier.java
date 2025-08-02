package com.ahmadda.infra.mail;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.infra.mail.exception.MailSendFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SmtpEmailNotifier implements EmailNotifier {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendEmails(final List<String> recipientEmails, final EventEmailPayload eventEmailPayload) {
        String subject = createSubject(eventEmailPayload.subject());
        String text = createText(eventEmailPayload.body());
        // TODO. 추후 BCC 방식으로 묶어서 전송 고려
        recipientEmails.forEach(recipientEmail -> {
            MimeMessage mimeMessage = createMimeMessage(recipientEmail, subject, text);

            javaMailSender.send(mimeMessage);
        });
    }

    private String createSubject(final EventEmailPayload.Subject subject) {
        return "[%s] %s님의 이벤트 안내: %s".formatted(
                subject.organizationName(),
                subject.organizerNickname(),
                subject.eventTitle()
        );
    }

    private String createText(final EventEmailPayload.Body body) {
        Context context = new Context();
        Map<String, Object> model = createModel(body);
        context.setVariables(model);

        return templateEngine.process("mail/event-notification", context);
    }

    private Map<String, Object> createModel(final EventEmailPayload.Body body) {
        Map<String, Object> model = new HashMap<>();
        model.put("organizationName", body.organizationName());
        model.put("content", body.content());
        model.put("title", body.title());
        model.put("organizerNickname", body.organizerNickname());
        model.put("place", body.place());
        model.put("registrationStart", body.registrationStart());
        model.put("registrationEnd", body.registrationEnd());
        model.put("eventStart", body.eventStart());
        model.put("eventEnd", body.eventEnd());
        model.put("eventId", body.eventId());

        return model;
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
