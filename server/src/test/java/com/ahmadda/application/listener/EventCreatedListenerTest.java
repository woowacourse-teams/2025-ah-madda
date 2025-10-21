package com.ahmadda.application.listener;

import com.ahmadda.application.dto.EventCreated;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatisticRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventCreatedListenerTest extends IntegrationTest {

    @Autowired
    private EventCreatedListener sut;

    @Autowired
    private EventStatisticRepository eventStatisticRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 이벤트가_생성되면_이벤트_통계가_저장된다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var group = createGroup();
        var organizationMember = createOrganizationMember(organization, member, group);
        var event = createEvent(organizationMember, organization);
        var eventCreated = new EventCreated(event.getId());

        // when
        sut.onEventCreated(eventCreated);

        // then
        var eventStatistic = eventStatisticRepository.findByEventId(event.getId());

        assertSoftly(softly -> {
            softly.assertThat(eventStatistic)
                    .isPresent();
            softly.assertThat(eventStatistic.get()
                            .getEvent())
                    .isEqualTo(event);
        });
    }

    @Test
    void 존재하지_않는_이벤트ID로_이벤트가_생성되면_예외가_발생한다() {
        // given
        var eventCreated = new EventCreated(999L);

        // when // then
        assertThatThrownBy(() -> sut.onEventCreated(eventCreated))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    private Organization createOrganization() {
        var organization = Organization.create("우테코", "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private Member createMember() {
        return memberRepository.save(Member.create("name", "ahmadda@ahmadda.com", "testPicture"));
    }

    private OrganizationMember createOrganizationMember(
            Organization organization,
            Member member,
            OrganizationGroup group
    ) {
        var organizationMember =
                OrganizationMember.create("surf", member, organization, OrganizationMemberRole.USER, group);

        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(final OrganizationMember organizationMember, final Organization organization) {
        var now = LocalDateTime.now();
        var event = Event.create(
                "title",
                "description",
                "place",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                10,
                false
        );

        return eventRepository.save(event);
    }

    private OrganizationGroup createGroup() {
        return organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
    }
}
