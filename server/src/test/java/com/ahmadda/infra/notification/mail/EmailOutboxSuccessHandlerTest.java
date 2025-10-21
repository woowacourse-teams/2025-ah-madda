package com.ahmadda.infra.notification.mail;

import com.ahmadda.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmailOutboxSuccessHandlerTest extends IntegrationTest {

    @Autowired
    private EmailOutboxSuccessHandler sut;

    @Autowired
    private EmailOutboxRepository emailOutboxRepository;

    @Autowired
    private EmailOutboxRecipientRepository emailOutboxRecipientRepository;

    @Test
    void 발송_성공시_해당_수신자를_삭제하고_남은_수신자는_유지된다() {
        // given
        var subject = "아맞다 이벤트 안내";
        var body = "이벤트에 참여해주셔서 감사합니다.";

        var outbox = emailOutboxRepository.save(EmailOutbox.createNow(subject, body));

        var recipients = List.of(
                EmailOutboxRecipient.create(outbox, "user1@email.com"),
                EmailOutboxRecipient.create(outbox, "user2@email.com")
        );
        emailOutboxRecipientRepository.saveAll(recipients);

        // when
        sut.handleSuccess("user1@email.com", subject, body);

        // then
        var remaining = emailOutboxRecipientRepository.findAll()
                .stream()
                .map(EmailOutboxRecipient::getRecipientEmail)
                .toList();

        assertThat(remaining).containsExactly("user2@email.com");

        // 아웃박스는 여전히 존재해야 함
        assertThat(emailOutboxRepository.findAll()).hasSize(1);
    }

    @Test
    void 모든_수신자가_삭제되면_아웃박스도_삭제된다() {
        // given
        var subject = "빈 아웃박스 테스트";
        var body = "본문";

        var outbox = emailOutboxRepository.save(EmailOutbox.createNow(subject, body));
        var recipient = EmailOutboxRecipient.create(outbox, "user1@email.com");
        emailOutboxRecipientRepository.save(recipient);

        // when
        sut.handleSuccess("user1@email.com", subject, body);

        // then
        assertThat(emailOutboxRecipientRepository.findAll()).isEmpty();
        assertThat(emailOutboxRepository.findAll()).isEmpty();
    }
}
