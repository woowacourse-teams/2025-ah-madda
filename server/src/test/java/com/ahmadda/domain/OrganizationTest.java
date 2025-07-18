package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getActiveEventsScenarios")
    void 활성_이벤트_목록을_정확히_반환한다(String description, List<Event> initialEvents, List<String> expectedTitles) {
        // given
        var sut = Organization.create("테스트 조직", "조직 설명", "image.png");
        initialEvents.forEach(sut::addEvent);

        // when
        var activeEvents = sut.getActiveEvents();

        // then
        assertThat(activeEvents).hasSize(expectedTitles.size());
        assertThat(activeEvents).extracting(Event::getTitle)
                .containsExactlyInAnyOrderElementsOf(expectedTitles);
    }

    private static Stream<Arguments> getActiveEventsScenarios() {
        var now = LocalDateTime.now();
        var pastEvent = createEvent("과거 이벤트", "설명", "장소", now.minusDays(2), now.minusDays(1));
        var futureEvent = createEvent("미래 이벤트", "설명", "장소", now.plusDays(1), now.plusDays(2));
        var futureEvent1 = createEvent("미래 이벤트 1", "설명", "장소", now.plusDays(1), now.plusDays(2));
        var futureEvent2 = createEvent("미래 이벤트 2", "설명", "장소", now.plusDays(3), now.plusDays(4));

        return Stream.of(
                Arguments.of("미래 이벤트만 있을 경우", List.of(futureEvent), List.of("미래 이벤트")),
                Arguments.of("과거 이벤트만 있을 경우", List.of(pastEvent), Collections.emptyList()),
                Arguments.of("이벤트가 아예 없는 경우", Collections.emptyList(), Collections.emptyList()),
                Arguments.of("과거와 미래 이벤트가 섞여 있을 경우",
                             List.of(pastEvent, futureEvent1, futureEvent2),
                             List.of("미래 이벤트 1", "미래 이벤트 2")
                )
        );
    }

    private static Event createEvent(String title, String description, String place,
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
