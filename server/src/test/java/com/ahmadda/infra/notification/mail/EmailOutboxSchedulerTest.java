package com.ahmadda.infra.notification.mail;

import com.ahmadda.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@IntegrationTest
class EmailOutboxSchedulerTest {

    @Autowired
    private EmailOutboxScheduler sut;

    @Autowired
    private EmailOutboxRepository emailOutboxRepository;

    @Autowired
    private EmailOutboxRecipientRepository emailOutboxRecipientRepository;

    @MockitoBean
    private EmailOutboxNotifier emailOutboxNotifier;

    @Test
    void 수신자가_존재하는_아웃박스는_재전송된다() {
        // given
        var outbox = EmailOutbox.create(
                "테스트 제목",
                "본문 내용",
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );
        emailOutboxRepository.save(outbox);
        var recipients = List.of(
                EmailOutboxRecipient.create(outbox, "a@test.com"),
                EmailOutboxRecipient.create(outbox, "b@test.com")
        );
        emailOutboxRecipientRepository.saveAll(recipients);

        // when
        sut.resendFailedEmails();

        // then
        verify(emailOutboxNotifier).sendFromOutbox(
                eq(List.of("a@test.com", "b@test.com")),
                eq("테스트 제목"),
                eq("본문 내용")
        );
    }

    @Test
    void 수신자가_없으면_아웃박스는_삭제된다() {
        // given
        var outbox = EmailOutbox.create(
                "빈 아웃박스",
                "내용 없음",
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );
        emailOutboxRepository.save(outbox);

        // when
        sut.resendFailedEmails();

        // then
        var remaining = emailOutboxRepository.findAll();
        assertSoftly(softly -> softly.assertThat(remaining)
                .isEmpty());
    }

    @Test
    void 만료된_락을_가진_아웃박스만_재전송된다() {
        // given
        var expired = EmailOutbox.create(
                "제목1",
                "본문1",
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );
        var expiredRecipient = EmailOutboxRecipient.create(expired, "expired@test.com");

        var fresh = EmailOutbox.create(
                "제목2",
                "본문2",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        var freshRecipient = EmailOutboxRecipient.create(fresh, "fresh@test.com");

        emailOutboxRepository.saveAll(List.of(expired, fresh));
        emailOutboxRecipientRepository.saveAll(List.of(expiredRecipient, freshRecipient));

        // when
        sut.resendFailedEmails();

        // then
        verify(emailOutboxNotifier).sendFromOutbox(
                eq(List.of("expired@test.com")),
                eq("제목1"),
                eq("본문1")
        );
        verify(emailOutboxNotifier, never()).sendFromOutbox(
                eq(List.of("fresh@test.com")),
                eq("제목2"),
                eq("본문2")
        );
    }

    @Test
    void 재전송된_아웃박스의_잠금_일시가_갱신된다() {
        // given
        var outbox = EmailOutbox.create(
                "락 갱신 테스트",
                "내용",
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );
        emailOutboxRepository.save(outbox);
        var recipient = EmailOutboxRecipient.create(outbox, "lock@test.com");
        emailOutboxRecipientRepository.save(recipient);
        var before = outbox.getLockedAt();

        // when
        sut.resendFailedEmails();

        // then
        var updated = emailOutboxRepository.findById(outbox.getId())
                .get();
        assertSoftly(softly ->
                softly.assertThat(updated.getLockedAt())
                        .isAfter(before)
        );
    }
}
