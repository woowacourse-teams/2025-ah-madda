package com.ahmadda.infra.notification.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BccChunkingEmailSenderTest {

    private BccChunkingEmailSender sut;
    private EmailSender delegate;

    @BeforeEach
    void setUp() {
        delegate = mock(EmailSender.class);
        sut = new BccChunkingEmailSender(delegate, 50);
    }

    @Test
    void 수신자가_제한_이하면_한번만_호출된다() {
        // given
        var recipients = createRecipientEmails(30);
        var subject = "이벤트 알림";
        var body = "본문 내용";

        // when
        sut.sendEmails(recipients, subject, body);

        // then
        verify(delegate, times(1)).sendEmails(recipients, subject, body);
    }

    @Test
    void 수신자가_제한을_초과하면_분할되어_여러번_호출된다() {
        // given
        var recipients = createRecipientEmails(120);
        var subject = "이벤트 알림";
        var body = "본문 내용";

        // when
        sut.sendEmails(recipients, subject, body);

        // then
        verify(delegate, times(3)).sendEmails(anyList(), any(), any());
    }

    private List<String> createRecipientEmails(int count) {
        var emails = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            emails.add("user" + i + "@example.com");
        }
        return emails;
    }
}
