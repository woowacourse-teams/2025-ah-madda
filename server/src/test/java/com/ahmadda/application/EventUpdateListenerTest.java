package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.EventUpdated;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatisticRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@IntegrationTest
class EventUpdateListenerTest {

    @Autowired
    private EventUpdateListener sut;

    @Autowired
    private EventStatisticRepository eventStatisticRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Test
    void 이벤트_기간이_연장되어_수정되면_연장된_기간만큼_통계가_추가_생성된다() {
        // given
        var now = LocalDateTime.now();
        var initialPeriod = EventOperationPeriod.create(now, now.plusDays(1), now.plusDays(2), now.plusDays(3), now);
        var organization = createOrganization();
        var organizer = createMember("organizer", "organizer@mail.com");
        var organizationMember = createOrganizationMember(organization, organizer);
        var event = createEvent(organizationMember, organization, initialPeriod);

        var extendedPeriod = EventOperationPeriod.create(now, now.plusDays(1), now.plusDays(4), now.plusDays(5), now);
        event.update(organizer, "title", "description", "place", extendedPeriod, 100);
        eventRepository.save(event);

        var eventUpdated = new EventUpdated(event.getId());

        // when
        sut.onEventUpdated(eventUpdated);

        var eventDuration = ChronoUnit.DAYS
                .between(
                        extendedPeriod.getRegistrationEventPeriod()
                                .start(),
                        extendedPeriod.getEventPeriod()
                                .end()
                ) + 1;

        // then
        var statisticAfterSecondUpdate = eventStatisticRepository.findByEventId(event.getId())
                .get();

        assertThat(statisticAfterSecondUpdate.getEventViewMetrics())
                .hasSize((int) eventDuration);
    }

    @Test
    void 이벤트자체가_생성되지_않았으면_예외가_발생한다() {
        //when //then
        assertThatThrownBy(() -> sut.onEventUpdated(new EventUpdated(-1L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다");
    }

    private Organization createOrganization() {
        var organization = Organization.create("우테코", "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private Member createMember(String name, String email) {
        var member = Member.create(name, email, "testPicture");

        return memberRepository.save(member);
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        var organizationMember = OrganizationMember.create("surf", member, organization, OrganizationMemberRole.USER);

        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(OrganizationMember organizer, Organization organization, EventOperationPeriod period) {
        var event = Event.create("title", "description", "place", organizer, organization, period, 100);

        return eventRepository.save(event);
    }
}
