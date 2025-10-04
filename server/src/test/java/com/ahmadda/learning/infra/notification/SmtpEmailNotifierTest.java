package com.ahmadda.learning.infra.notification;

import com.ahmadda.annotation.LearningTest;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.notification.ReminderEmail;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import com.ahmadda.infra.notification.mail.config.SmtpProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Disabled
@LearningTest
class SmtpEmailNotifierTest {

    private SmtpEmailNotifier sut;

    @Autowired
    private SmtpProperties smtpProperties;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private NotificationProperties notificationProperties;

    @BeforeEach
    void setUp() {
        sut = createSmtpEmailNotifier("google");
    }

    private SmtpEmailNotifier createSmtpEmailNotifier(String provider) {
        SmtpProperties.Account acc =
                switch (provider.toLowerCase()) {
                    case "google" -> smtpProperties.getGoogle();
                    case "aws" -> smtpProperties.getAws();
                    default -> throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
                };

        JavaMailSender sender = createJavaMailSender(acc);
        return new SmtpEmailNotifier(sender, templateEngine, notificationProperties);
    }

    private JavaMailSender createJavaMailSender(SmtpProperties.Account acc) {
        var sender = new JavaMailSenderImpl();
        sender.setHost(acc.getHost());
        sender.setPort(acc.getPort());
        sender.setUsername(acc.getUsername());
        sender.setPassword(acc.getPassword());
        sender.setDefaultEncoding("UTF-8");
        if (acc.getProperties() != null) {
            sender.getJavaMailProperties()
                    .putAll(acc.getProperties());
        }
        return sender;
    }

    @Test
    void 실제_SMTP로_메일을_발송한다() {
        // given
        var organizationName = "테스트 이벤트 스페이스";
        var eventTitle = "테스트 이벤트";
        var organizerNickname = "주최자";

        var payload = new EventEmailPayload(
                new EventEmailPayload.Subject(organizationName, eventTitle),
                new EventEmailPayload.Body(
                        "테스트 메일 본문입니다.",
                        organizationName,
                        eventTitle,
                        organizerNickname,
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L,
                        1L
                )
        );

        var reminderEmail = new ReminderEmail(
                List.of("amadda.team@gmail.com"),
                payload
        );

        // when // then
        sut.remind(reminderEmail);
    }

    // Gmail: BCC 최대 100명
    // AWS: BCC 최대 50명
    @Test
    void BCC_수신자_허용_범위를_초과하면_예외가_발생한다() {
        // given
        var organizationName = "테스트 이벤트 스페이스";
        var eventTitle = "테스트 이벤트";
        var organizerNickname = "주최자";

        var recipients = new ArrayList<String>();
        for (int i = 0; i < 120; i++) {
            recipients.add("dummy" + i + "@example.com");
        }

        var payload = new EventEmailPayload(
                new EventEmailPayload.Subject(organizationName, eventTitle),
                new EventEmailPayload.Body(
                        "100명 이상의 수신자에게 발송되는 테스트 메일입니다.",
                        organizationName,
                        eventTitle,
                        organizerNickname,
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L,
                        1L
                )
        );

        var reminderEmail = new ReminderEmail(recipients, payload);

        // when // then
        sut.remind(reminderEmail);
    }
}
