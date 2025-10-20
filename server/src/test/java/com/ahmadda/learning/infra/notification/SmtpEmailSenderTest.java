package com.ahmadda.learning.infra.notification;

import com.ahmadda.annotation.LearningTest;
import com.ahmadda.infra.notification.mail.EmailOutboxSuccessHandler;
import com.ahmadda.infra.notification.mail.SmtpEmailSender;
import com.ahmadda.infra.notification.mail.config.SmtpProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.List;

@Disabled
@LearningTest
class SmtpEmailSenderTest {

    private SmtpEmailSender sut;

    @Autowired
    private SmtpProperties smtpProperties;

    @Autowired
    private EmailOutboxSuccessHandler emailOutboxSuccessHandler;

    @BeforeEach
    void setUp() {
        sut = createSmtpEmailSender("google");
    }

    private SmtpEmailSender createSmtpEmailSender(String provider) {
        SmtpProperties.Account acc =
                switch (provider.toLowerCase()) {
                    case "google" -> smtpProperties.getGoogle();
                    case "aws" -> smtpProperties.getAws();
                    default -> throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
                };

        JavaMailSender sender = createJavaMailSender(acc);
        return new SmtpEmailSender(sender, emailOutboxSuccessHandler);
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
        var recipients = List.of("amadda.team@gmail.com");
        var subject = "테스트 이메일 발송";
        var body = """
                <h3>테스트 메일입니다.</h3>
                <p>이 메일은 학습용 SMTP 테스트에서 발송되었습니다.</p>
                """;

        // when & then
        sut.sendEmails(recipients, subject, body);
    }

    // Gmail: BCC 최대 100명
    // AWS: BCC 최대 50명
    @Test
    void BCC_수신자_허용_범위를_초과하면_예외가_발생한다() {
        // given
        var recipients = new ArrayList<String>();
        for (int i = 0; i < 120; i++) {
            recipients.add("dummy" + i + "@example.com");
        }

        var subject = "테스트 BCC 한도 초과";
        var body = """
                <p>이 테스트는 Gmail의 BCC 수신자 제한(100명)을 초과하는 경우의 동작을 확인하기 위한 것입니다.</p>
                """;

        // when & then
        sut.sendEmails(recipients, subject, body);
    }
}
