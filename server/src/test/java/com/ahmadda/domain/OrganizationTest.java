package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrganizationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void 조직_생성시_설명이_비어있으면_예외가_발생한다(String blankDescription) {
        // when // then
        assertThatThrownBy(() -> Organization.create("정상 이름", blankDescription, "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 조직_생성시_이름이_규칙보다_길면_예외가_발생한다() {
        // given
        var longName = "스무글자를넘어가는엄청나게긴조직이름입니다";

        // when // then
        assertThatThrownBy(() -> Organization.create(longName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 조직_생성시_이름이_규칙보다_짧으면_예외가_발생한다() {
        // given
        var shortName = "한";

        // when // then
        assertThatThrownBy(() -> Organization.create(shortName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void 조직_생성시_이름이_비어있으면_예외가_발생한다(String blankName) {
        // when // then
        assertThatThrownBy(() -> Organization.create(blankName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 과거_이벤트는_활성_이벤트에서_제외된다() {
        // given
        var sut = Organization.create("테스트 조직", "조직 설명", "image.png");
        var now = LocalDateTime.now();

        var pastEvent = createEvent(
                "과거 이벤트",
                "과거 이벤트 설명",
                "과거 장소",
                now.minusDays(2), // eventStart: 2일 전
                now.minusDays(1)  // eventEnd: 1일 전
        );

        sut.addEvent(pastEvent);

        // when
        var activeEvents = sut.getActiveEvents();

        // then
        assertThat(activeEvents).isEmpty();
    }

    @Test
    void 미래_이벤트는_활성_이벤트로_반환된다() {
        // given
        var sut = Organization.create("테스트 조직", "조직 설명", "image.png");
        var now = LocalDateTime.now();

        var futureEvent = createEvent(
                "미래 이벤트",
                "미래 이벤트 설명",
                "미래 장소",
                now.plusDays(1), // eventStart: 1일 후
                now.plusDays(2)  // eventEnd: 2일 후
        );

        sut.addEvent(futureEvent);

        // when
        var activeEvents = sut.getActiveEvents();

        // then
        assertThat(activeEvents).hasSize(1);
        assertThat(activeEvents.get(0).getTitle()).isEqualTo("미래 이벤트");
    }

    @Test
    void 활성_이벤트가_없으면_빈_리스트를_반환한다() {
        // given
        var sut = Organization.create("테스트 조직", "조직 설명", "image.png");

        // when
        var activeEvents = sut.getActiveEvents();

        // then
        assertThat(activeEvents).isEmpty();
    }

    @Test
    void 과거와_미래_이벤트가_섞여있을_때_미래_이벤트만_반환한다() {
        // given
        var sut = Organization.create("테스트 조직", "조직 설명", "image.png");
        var now = LocalDateTime.now();

        // 과거 이벤트
        var pastEvent = createEvent(
                "과거 이벤트",
                "과거 이벤트 설명",
                "과거 장소",
                now.minusDays(3),
                now.minusDays(2)
        );

        // 미래 이벤트 1
        var futureEvent1 = createEvent(
                "미래 이벤트 1",
                "미래 이벤트 1 설명",
                "미래 장소 1",
                now.plusDays(1),
                now.plusDays(2)
        );

        // 미래 이벤트 2
        var futureEvent2 = createEvent(
                "미래 이벤트 2",
                "미래 이벤트 2 설명",
                "미래 장소 2",
                now.plusDays(3),
                now.plusDays(4)
        );

        sut.addEvent(pastEvent);
        sut.addEvent(futureEvent1);
        sut.addEvent(futureEvent2);

        // when
        var activeEvents = sut.getActiveEvents();

        // then
        assertThat(activeEvents).hasSize(2);
        assertThat(activeEvents).extracting(Event::getTitle)
                .containsExactlyInAnyOrder("미래 이벤트 1", "미래 이벤트 2");
    }

    private Event createEvent(String title, String description, String place,
                              LocalDateTime eventStart, LocalDateTime eventEnd) {
        var member = Member.create("테스트 멤버", "test@example.com");
        var organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        var organizer = OrganizationMember.create("주최자", member, organization);

        return Event.create(
                title,
                description,
                place,
                organizer,
                organization,
                eventStart.minusDays(10), // registrationStart
                eventStart.minusDays(1),  // registrationEnd
                eventStart,
                eventEnd,
                50
        );
    }
}
