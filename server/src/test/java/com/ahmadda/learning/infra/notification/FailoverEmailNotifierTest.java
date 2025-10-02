package com.ahmadda.learning.infra.notification;

import com.ahmadda.annotation.LearningTest;
import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.notification.ReminderEmail;
import com.ahmadda.infra.notification.mail.FailoverEmailNotifier;
import com.ahmadda.infra.notification.mail.NoopEmailNotifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSendException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled
@LearningTest
@Import(FailoverEmailNotifierTest.TestMailConfig.class)
class FailoverEmailNotifierTest {

    @Autowired
    @Qualifier("testFailoverEmailNotifier")
    private FailoverEmailNotifier sut;

    @Autowired
    @Qualifier("primaryNotifier")
    private EmailNotifier primaryNotifier;

    @Autowired
    @Qualifier("secondaryNotifier")
    private EmailNotifier secondaryNotifier;

    @Test
    void 실제_Failover_구조에서_메일을_발송한다() {
        var payload = createPayload("테스트 이벤트 스페이스", "테스트 이벤트", "주최자닉네임");
        var reminderEmail = new ReminderEmail(List.of("test@example.com"), payload);

        sut.sendEmail(reminderEmail);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(primaryNotifier, times(1)).sendEmail(any(ReminderEmail.class)));
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(secondaryNotifier, times(0)).sendEmail(any(ReminderEmail.class)));
    }

    @Test
    void PrimarySMTP_실패시_SecondarySMTP로_Failover한다() {
        var payload = createPayload("테스트 이벤트 스페이스", "이벤트", "닉네임");
        var reminderEmail = new ReminderEmail(List.of("test@example.com"), payload);

        doThrow(new MailSendException("primary 실패"))
                .when(primaryNotifier)
                .sendEmail(any(ReminderEmail.class));

        sut.sendEmail(reminderEmail);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(primaryNotifier, times(1)).sendEmail(any(ReminderEmail.class)));
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(secondaryNotifier, times(1)).sendEmail(any(ReminderEmail.class)));
    }

    @Test
    void PrimarySMTP가_설정한_실패_횟수만큼_실패하면_CircuitBreaker가_OPEN되어_바로_Fallback한다() throws Exception {
        var payload = createPayload("테스트 이벤트 스페이스", "이벤트", "닉네임");
        var reminderEmail = new ReminderEmail(List.of("test@example.com"), payload);

        doThrow(new MailSendException("primary 실패"))
                .when(primaryNotifier)
                .sendEmail(any(ReminderEmail.class));

        // 3번 실패 → CircuitBreaker OPEN
        for (int i = 0; i < 3; i++) {
            try {
                sut.sendEmail(reminderEmail);
                Thread.sleep(7000);
            } catch (Exception ignored) {
            }
        }

        sut.sendEmail(reminderEmail);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(primaryNotifier, times(3)).sendEmail(any(ReminderEmail.class)));
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(secondaryNotifier, times(4)).sendEmail(any(ReminderEmail.class)));
    }

    private EventEmailPayload createPayload(
            final String organizationName,
            final String eventTitle,
            final String organizerNickname
    ) {
        return new EventEmailPayload(
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
    }

    // CircuitBreaker를 위해 별도 테스트용 Config 필요
    @TestConfiguration
    static class TestMailConfig {

        @Bean
        FailoverEmailNotifier testFailoverEmailNotifier(
                @Qualifier("primaryNotifier") EmailNotifier primary,
                @Qualifier("secondaryNotifier") EmailNotifier secondary
        ) {
            return new FailoverEmailNotifier(primary, secondary);
        }

        @Bean
        EmailNotifier primaryNotifier() {
            return spy(new NoopEmailNotifier());
        }

        @Bean
        EmailNotifier secondaryNotifier() {
            return spy(new NoopEmailNotifier());
        }
    }
}
