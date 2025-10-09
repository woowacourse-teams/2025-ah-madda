package com.ahmadda.infra.notification.mail;

import com.ahmadda.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class EmailOutboxSuccessHandlerTest {

    @Autowired
    private EmailOutboxSuccessHandler sut;

    @Autowired
    private EmailOutboxRepository emailOutboxRepository;

    @Autowired
    private EmailOutboxRecipientRepository emailOutboxRecipientRepository;

    @Test
    void 발송_성공시_해당_수신자를_삭제한다() {
        // given
        var subject = "아맞다 이벤트 안내";
        var body = "이벤트에 참여해주셔서 감사합니다.";
        var recipients = List.of("user1@email.com", "user2@email.com");

        emailOutboxRepository.save(EmailOutbox.createNow(subject, body, recipients));

        // when
        sut.handleSuccess("user1@email.com", subject, body);

        // then
        var remainingRecipients = emailOutboxRecipientRepository.findAll()
                .stream()
                .map(EmailOutboxRecipient::getRecipientEmail)
                .toList();
        assertThat(remainingRecipients).containsExactly("user2@email.com");
    }
}
