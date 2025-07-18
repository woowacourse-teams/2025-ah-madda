package com.ahmadda.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class OrganizationTest {

    private Organization sut;
    private OrganizationMember organizer;

    @BeforeEach
    void setUp() {
        sut = Organization.create("테스트 조직", "조직 설명", "image.png");
        var member = Member.create("주최자 멤버", "organizer@example.com");
        organizer = OrganizationMember.create("주최자", member, sut);
    }

    @Test
    void 활성화된_이벤트_목록을_조회한다() {
        // given
        var now = LocalDateTime.now();
        var pastEvent = createEventForTest("과거 이벤트", now.minusDays(2), now.minusDays(1));
        var activeEvent1 = createEventForTest("활성 이벤트 1", now.plusDays(1), now.plusDays(2));
        var activeEvent2 = createEventForTest("활성 이벤트 2", now.plusDays(3), now.plusDays(4));

        // when
        List<Event> activeEvents = sut.getActiveEvents();

        // then
        assertSoftly(softly -> {
            softly.assertThat(activeEvents).hasSize(2);
            softly.assertThat(activeEvents).extracting(Event::getTitle)
                    .containsExactlyInAnyOrder("활성 이벤트 1", "활성 이벤트 2");
        });
    }

    private Event createEventForTest(String title, LocalDateTime start, LocalDateTime end) {
        return Event.create(
                title, "설명", "장소", organizer, sut,
                start.minusDays(5), start.minusDays(1),
                start, end,
                50
        );
    }
}