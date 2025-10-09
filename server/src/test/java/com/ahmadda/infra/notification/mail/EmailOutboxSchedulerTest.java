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

    @MockitoBean
    private EmailOutboxNotifier emailOutboxNotifier;

    @Test
    void 수신자가_존재하는_Outbox는_재전송된다() {
        // given
        var outbox = EmailOutbox.create(
                "테스트 제목",
                "본문 내용",
                List.of("a@test.com", "b@test.com"),
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );
        emailOutboxRepository.save(outbox);

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
    void 수신자가_없으면_Outbox는_삭제된다() {
        // given
        var outbox = EmailOutbox.create(
                "빈 아웃박스",
                "내용 없음",
                List.of(),
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
    void 만료된_락을_가진_Outbox만_재전송된다() {
        // given
        var expired = EmailOutbox.create(
                "제목1",
                "본문1",
                List.of("expired@test.com"),
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );

        var fresh = EmailOutbox.create(
                "제목2",
                "본문2",
                List.of("fresh@test.com"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        emailOutboxRepository.saveAll(List.of(expired, fresh));

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
    void 재전송된_Outbox의_lockedAt이_갱신된다() {
        // given
        var outbox = EmailOutbox.create(
                "락 갱신 테스트",
                "내용",
                List.of("lock@test.com"),
                LocalDateTime.now()
                        .minusMinutes(10),
                LocalDateTime.now()
                        .minusMinutes(20)
        );
        emailOutboxRepository.save(outbox);
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
