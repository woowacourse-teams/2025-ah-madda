package com.ahmadda.domain.notification;

import com.ahmadda.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class EventEmailPayloadTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void 제목_렌더링시_조직명과_이벤트명이_포함된다() {
        // given
        var subject = createSubject();
        var body = createBody();
        var payload = new EventEmailPayload(subject, body);

        // when
        var renderedSubject = payload.renderSubject();

        // then
        assertSoftly(softly -> {
            softly.assertThat(renderedSubject)
                    .contains("[아맞다]")
                    .contains(subject.organizationName())
                    .contains(subject.eventTitle())
                    .contains("이벤트 안내");
        });
    }

    @Test
    void 본문_렌더링시_Body의_모든_필드가_포함된다() {
        // given
        var subject = createSubject();
        var body = createBody();
        var payload = new EventEmailPayload(subject, body);

        // when
        var renderedBody = payload.renderBody(templateEngine, "https://ahmadda.com/org/");

        // then
        assertSoftly(softly -> {
            softly.assertThat(renderedBody)
                    .contains(body.content())
                    .contains(body.organizationName())
                    .contains(body.title())
                    .contains(body.organizerNickname())
                    .contains(body.place())
                    .contains(body.registrationStart()
                            .format(FORMATTER))
                    .contains(body.registrationEnd()
                            .format(FORMATTER))
                    .contains(body.eventStart()
                            .format(FORMATTER))
                    .contains(body.eventEnd()
                            .format(FORMATTER))
                    .contains(String.valueOf(body.organizationId()))
                    .contains(String.valueOf(body.eventId()));
        });
    }

    private EventEmailPayload.Subject createSubject() {
        return new EventEmailPayload.Subject(
                "아맞다 스페이스",
                "개발자 밋업"
        );
    }

    private EventEmailPayload.Body createBody() {
        return new EventEmailPayload.Body(
                "반갑습니다. 아맞다 밋업 안내입니다.",
                "아맞다 스페이스",
                "개발자 밋업",
                "머피,훌라",
                "잠실 D타워",
                LocalDateTime.now()
                        .minusDays(3),
                LocalDateTime.now()
                        .minusDays(1),
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2),
                1L,
                2L
        );
    }
}
