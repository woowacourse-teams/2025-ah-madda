package com.ahmadda.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationMemberTest {

    private Member member;
    private Organization organization;
    private OrganizationMember sut;

    @BeforeEach
    void setUp() {
        member = Member.create("테스트 회원", "test@example.com");
        organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        sut = OrganizationMember.create("주최자", member, organization);
    }

    @Test
    void 게스트로_참여한_이벤트_목록을_조회한다() {
        // given
        var participantMember = Member.create("참여자", "participant@example.com");
        var participant = OrganizationMember.create("참여자 조직원", participantMember, organization);

        var event1 = createEventForTest("이벤트 1");
        var event2 = createEventForTest("이벤트 2");
        var event3 = createEventForTest("이벤트 3");

        Guest.create(event1, participant, event1.getRegistrationStart());
        Guest.create(event3, participant, event3.getRegistrationStart());

        // when
        List<Event> participatedEvents = participant.getParticipatedEvents();

        // then
        assertThat(participatedEvents).hasSize(2);
        assertThat(participatedEvents).extracting(Event::getTitle)
                .containsExactlyInAnyOrder("이벤트 1", "이벤트 3");
    }

    private Event createEventForTest(String title) {
        var now = LocalDateTime.now();
        return Event.create(
                title, "설명", "장소", sut, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(5),
                        now.plusDays(10), now.plusDays(11),
                        now
                ),
                "이벤트 근로",
                50
        );
    }
}
