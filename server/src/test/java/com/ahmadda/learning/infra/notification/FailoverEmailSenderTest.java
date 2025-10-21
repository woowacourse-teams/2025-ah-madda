package com.ahmadda.learning.infra.notification;

import com.ahmadda.infra.notification.mail.EmailSender;
import com.ahmadda.infra.notification.mail.FailoverEmailSender;
import com.ahmadda.infra.notification.mail.NoopEmailSender;
import com.ahmadda.support.LearningTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSendException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled
@LearningTest
@Import(FailoverEmailSenderTest.TestMailConfig.class)
class FailoverEmailSenderTest {

    @Autowired
    @Qualifier("testFailoverEmailSender")
    private FailoverEmailSender sut;

    @Autowired
    @Qualifier("primarySender")
    private EmailSender primarySender;

    @Autowired
    @Qualifier("secondarySender")
    private EmailSender secondarySender;

    private final List<String> recipients = List.of("test@example.com");
    private final String subject = "테스트 이메일 제목";
    private final String body = "테스트 이메일 본문입니다.";

    @Test
    void 실제_Failover_구조에서_정상적으로_메일을_발송한다() {
        sut.sendEmails(recipients, subject, body);

        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(primarySender, times(1))
                        .sendEmails(recipients, subject, body));
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(secondarySender, times(0))
                        .sendEmails(anyList(), anyString(), anyString()));
    }

    @Test
    void PrimarySMTP_실패시_SecondarySMTP로_Failover한다() {
        doThrow(new MailSendException("primary 실패"))
                .when(primarySender)
                .sendEmails(recipients, subject, body);

        sut.sendEmails(recipients, subject, body);

        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(primarySender, times(1))
                        .sendEmails(recipients, subject, body));
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(secondarySender, times(1))
                        .sendEmails(recipients, subject, body));
    }

    @Test
    void PrimarySMTP가_연속_실패하면_CircuitBreaker가_OPEN되어_Secondary로_즉시_Fallback한다() throws Exception {
        doThrow(new MailSendException("primary 실패"))
                .when(primarySender)
                .sendEmails(recipients, subject, body);

        // 3회 실패 → CircuitBreaker OPEN
        for (int i = 0; i < 3; i++) {
            try {
                sut.sendEmails(recipients, subject, body);
                Thread.sleep(7000); // CircuitBreaker transition을 위한 대기
            } catch (Exception ignored) {
            }
        }

        // 4번째 호출 → 바로 secondary로 fallback
        sut.sendEmails(recipients, subject, body);

        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(primarySender, times(3))
                        .sendEmails(recipients, subject, body));
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(secondarySender, times(4))
                        .sendEmails(recipients, subject, body));
    }

    @TestConfiguration
    static class TestMailConfig {

        @Bean
        FailoverEmailSender testFailoverEmailSender(
                @Qualifier("primarySender") EmailSender primary,
                @Qualifier("secondarySender") EmailSender secondary
        ) {
            return new FailoverEmailSender(primary, secondary);
        }

        @Bean
        EmailSender primarySender() {
            return spy(new NoopEmailSender());
        }

        @Bean
        EmailSender secondarySender() {
            return spy(new NoopEmailSender());
        }
    }
}
