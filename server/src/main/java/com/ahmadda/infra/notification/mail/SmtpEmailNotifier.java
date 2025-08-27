package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.infra.notification.config.NotificationProperties;
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
    private final NotificationProperties notificationProperties;

    @Async
    @Override
    public void sendEmails(final List<OrganizationMember> recipients, final EventEmailPayload eventEmailPayload) {
        List<String> recipientEmails = getRecipientEmails(recipients);
        String subject = createSubject(eventEmailPayload.subject());
        String text = createText(eventEmailPayload.body());
        // TODO. 추후 BCC 방식으로 묶어서 전송 고려
        recipientEmails.forEach(recipientEmail -> {
            MimeMessage mimeMessage = createMimeMessage(recipientEmail, subject, text);

            // TODO: 추후 지수 백오프를 이용한 재시도 로직 구현
            javaMailSender.send(mimeMessage);
        });
    }

    private List<String> getRecipientEmails(List<OrganizationMember> recipients) {
        return recipients.stream()
                .map(organizationMember -> organizationMember.getMember()
                        .getEmail())
                .toList();
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
        model.put("redirectUrl", notificationProperties.getRedirectUrlPrefix() + body.eventId());

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
        }

        return mimeMessage;
    }
}
