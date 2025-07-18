package com.ahmadda.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GuestTest {

    private Member member;
    private Event event;
    private OrganizationMember participant;
    private OrganizationMember otherParticipant;

    @BeforeEach
    void setUp() {
        var organizerMember = Member.create("주최자 멤버", "organizer@example.com");
        var organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        var organizer = OrganizationMember.create("주최자", organizerMember, organization);
        var now = LocalDateTime.now();
        event = Event.create(
                "테스트 이벤트", "설명", "장소", organizer, organization,
                now.plusDays(1), now.plusDays(5),
                now.plusDays(10), now.plusDays(11),
                50
        );
        member = Member.create("참가자 멤버", "guest@example.com");
        participant = OrganizationMember.create("참가자", member, organization);
        otherParticipant =
                OrganizationMember.create("다른 참가자", Member.create("다른 멤버", "other@example.com"), organization);
    }

    @Test
    void 동일한_참가자인지_확인한다() {
        // given
        var guest = Guest.create(event, participant);

        // when
        var isSame = guest.isSameParticipant(participant);

        // then
        assertThat(isSame).isTrue();
    }

    @Test
    void 다른_참가자인지_확인한다() {
        // given
        var guest = Guest.create(event, participant);

        // when
        var isSame = guest.isSameParticipant(otherParticipant);

        // then
        assertThat(isSame).isFalse();
    }
}
