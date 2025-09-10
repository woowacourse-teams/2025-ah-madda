package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.infra.notification.config.NotificationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
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
    private final EntityManager em;

    @Async
    @Transactional(readOnly = true)
    @Override
    public void sendEmails(final List<OrganizationMember> recipients, final EventEmailPayload eventEmailPayload) {
        List<OrganizationMember> mergedRecipients = recipients.stream()
                .map(em::merge)
                .toList();

        List<String> recipientEmails = getRecipientEmails(mergedRecipients);
        if (recipientEmails.isEmpty()) {
            return;
        }

        String subject = createSubject(eventEmailPayload.subject());
        String text = createText(eventEmailPayload.body());

        MimeMessage mimeMessage = createMimeMessageWithBcc(recipientEmails, subject, text);
        // TODO: 추후 지수 백오프를 이용한 재시도 로직 구현
        javaMailSender.send(mimeMessage);
    }

    private List<String> getRecipientEmails(List<OrganizationMember> recipients) {
        return recipients.stream()
                .map(organizationMember -> organizationMember.getMember()
                        .getEmail())
                .toList();
    }

    private String createSubject(final EventEmailPayload.Subject subject) {
        return "[아맞다] %s의 이벤트 안내: %s".formatted(
                subject.organizationName(),
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
        model.put(
                "redirectUrl",
                notificationProperties.getRedirectUrlPrefix() + body.organizationId() + "/event/" + body.eventId()
        );

        return model;
    }

    private MimeMessage createMimeMessageWithBcc(
            final List<String> bccRecipients,
            final String subject,
            final String text
    ) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo("amadda.team@gmail.com");
            // TODO. 추후 BCC 수신자가 100명 이상일 경우, 배치 처리 고려
            helper.setBcc(bccRecipients.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            log.error("mailError : {} ", e.getMessage(), e);
        }

        return mimeMessage;
    }
}
