package com.ahmadda.infra.notification.mail;

import com.ahmadda.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.IllegalTransactionStateException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class OutboxEmailSenderTest {

    @Autowired
    private OutboxEmailSender sut;

    @Autowired
    private EmailOutboxRepository emailOutboxRepository;

    @Autowired
    private EmailOutboxRecipientRepository emailOutboxRecipientRepository;

    @MockitoBean
    @Qualifier("failoverEmailSender")
    private EmailSender delegate;

    @Test
    void 트랜잭션이_없으면_MANDATORY_예외가_발생한다() {
        // given
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }

        var recipients = List.of("no@test.com");
        var subject = "subject";
        var body = "body";

        // when // then
        assertThatThrownBy(() -> sut.sendEmails(recipients, subject, body))
                .isInstanceOf(IllegalTransactionStateException.class);

        if (!TestTransaction.isActive()) {
            TestTransaction.start();
        }
    }

    @Test
    void 트랜잭션_내에서_Outbox와_수신자가_저장된다() {
        // given
        var recipients = List.of("a@test.com", "b@test.com");
        var subject = "subject";
        var body = "body";

        // when
        sut.sendEmails(recipients, subject, body);

        // then
        var savedOutboxes = emailOutboxRepository.findAll();
        var savedRecipients = emailOutboxRecipientRepository.findAll();

        assertSoftly(softly -> {
            softly.assertThat(savedOutboxes)
                    .hasSize(1);
            var outbox = savedOutboxes.get(0);
            softly.assertThat(outbox.getSubject())
                    .isEqualTo(subject);
            softly.assertThat(outbox.getBody())
                    .isEqualTo(body);

            softly.assertThat(savedRecipients)
                    .hasSize(2);
            softly.assertThat(savedRecipients)
                    .extracting(EmailOutboxRecipient::getRecipientEmail)
                    .containsExactlyInAnyOrder("a@test.com", "b@test.com");
        });
    }

    @Test
    void 커밋_후에만_delegate_전송이_실행된다() {
        // given
        var recipients = List.of("c@test.com", "d@test.com");
        var subject = "title";
        var body = "body";

        // when
        sut.sendEmails(recipients, subject, body);

        // then
        verify(delegate, never()).sendEmails(anyList(), anyString(), anyString());

        // afterCommit 트리거
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // 커밋 후 delegate 호출 확인
        verify(delegate, times(1)).sendEmails(recipients, subject, body);
    }
}
