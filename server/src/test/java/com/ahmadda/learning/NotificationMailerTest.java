package com.ahmadda.learning;

import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.NotificationMailer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "mail.mock=false")
@Transactional
class NotificationMailerTest {

    @Autowired
    private NotificationMailer notificationMailer;

    @Test
    void 실제_SMTP로_메일을_발송한다() {
        // given
        var organizationName = "테스트 조직";
        var eventTitle = "테스트 이벤트";
        var organizerNickname = "주최자";
        var recipientEmail = "amadda.team@gmail.com";

        var emailPayload = new EventEmailPayload(
                new EventEmailPayload.Subject(
                        organizationName,
                        organizerNickname,
                        eventTitle
                ),
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
                        1L
                )
        );

        // when // then
        notificationMailer.sendEmail(recipientEmail, emailPayload);
    }
}
