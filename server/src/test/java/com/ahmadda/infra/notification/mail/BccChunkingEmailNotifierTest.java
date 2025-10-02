package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.notification.ReminderEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BccChunkingEmailNotifierTest {

    private BccChunkingEmailNotifier sut;
    private EmailNotifier delegate;
    private EventEmailPayload payload;

    @BeforeEach
    void setUp() {
        delegate = mock(EmailNotifier.class);
        sut = new BccChunkingEmailNotifier(delegate, 50);

        payload = new EventEmailPayload(
                new EventEmailPayload.Subject("이벤트 스페이스", "이벤트"),
                new EventEmailPayload.Body(
                        "본문",
                        "이벤트 스페이스",
                        "이벤트",
                        "주최자",
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L, 1L
                )
        );
    }

    @Test
    void 수신자가_여러명일때_제한_이하면_한번만_호출된다() {
        // given
        var recipients = createRecipientEmails(30);
        var reminderEmail = new ReminderEmail(recipients, payload);

        // when
        sut.sendEmail(reminderEmail);

        // then
        verify(delegate, times(1)).sendEmail(reminderEmail);
    }

    @Test
    void 수신자가_여러명일때_제한을_초과하면_분할되어_여러번_호출된다() {
        // given
        var recipients = createRecipientEmails(120);
        var reminderEmail = new ReminderEmail(recipients, payload);

        // when
        sut.sendEmail(reminderEmail);

        // then
        verify(delegate, times(3)).sendEmail(any(ReminderEmail.class));
    }

    private List<String> createRecipientEmails(int count) {
        var emails = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            emails.add("user" + i + "@example.com");
        }
        return emails;
    }
}
