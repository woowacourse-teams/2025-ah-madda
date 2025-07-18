package com.ahmadda.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventTest {

    private Member member;
    private Organization organization;
    private OrganizationMember organizer;

    @BeforeEach
    void setUp() {
        member = Member.create("테스트 멤버", "test@example.com");
        organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        organizer = OrganizationMember.create("주최자", member, organization);
    }

    @Test
    void 게스트가_이벤트에_참여했는지_알_수_있다() {
        // given
        var event = createTestEvent();
        var member1 = Member.create("참가자1", "guest1@example.com");
        var member2 = Member.create("참가자2", "guest2@example.com");
        var organizationMember = OrganizationMember.create("조직원", member1, organization);
        var anotherOrganizationMember = OrganizationMember.create("다른 조직원", member2, organization);
        event.getGuests().add(Guest.create(event, organizationMember));

        // when
        var actual = event.hasGuest(organizationMember);
        var unexpected = event.hasGuest(anotherOrganizationMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual).isTrue();
            softly.assertThat(unexpected).isFalse();
        });
    }

    private Event createTestEvent() {
        var now = LocalDateTime.now();
        return Event.create(
                "테스트 이벤트", "설명", "장소", organizer, organization,
                now.plusDays(1), now.plusDays(5),
                now.plusDays(10), now.plusDays(11),
                50
        );
    }
}
