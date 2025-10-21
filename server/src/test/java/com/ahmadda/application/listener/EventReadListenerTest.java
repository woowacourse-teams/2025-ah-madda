package com.ahmadda.application.listener;

import com.ahmadda.application.dto.EventRead;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatistic;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventReadListenerTest extends IntegrationTest {

    @Autowired
    private EventReadListener sut;

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

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 해당_이벤트에_대한_통계가_존재하면_조회수가_1_증가한다() {
        // given
        var organization = createOrganization();
        var organizer = createMember("organizer", "organizer@mail.com");
        var group = createGroup();
        var organizationMember = createOrganizationMember(organization, organizer, group);
        var event = createEvent(organizationMember, organization);
        eventStatisticRepository.save(EventStatistic.create(event));

        var eventRead = new EventRead(event.getId());

        // when
        sut.onEventRead(eventRead);

        // then
        var eventStatistic = eventStatisticRepository.findByEventId(event.getId())
                .get();

        var viewCount = eventStatistic.getEventViewMetrics()
                .stream()
                .filter(metric -> metric.isSameDate(LocalDate.now()))
                .findFirst()
                .get()
                .getViewCount();
        assertThat(viewCount).isEqualTo(1);
    }

    @Test
    void 해당_이벤트에_대한_통계가_존재하지_않으면_통계를_생성하고_조회수를_1로_만든다() {
        // given
        var organization = createOrganization();
        var organizer = createMember("organizer", "organizer@mail.com");
        var group = createGroup();
        var organizationMember = createOrganizationMember(organization, organizer, group);
        var event = createEvent(organizationMember, organization);

        var eventRead = new EventRead(event.getId());

        // when
        sut.onEventRead(eventRead);

        // then
        var eventStatistic = eventStatisticRepository.findByEventId(event.getId())
                .get();
        var viewCount = eventStatistic.getEventViewMetrics()
                .stream()
                .filter(metric -> metric.isSameDate(LocalDate.now()))
                .findFirst()
                .get()
                .getViewCount();
        assertThat(viewCount).isEqualTo(1);
    }

    @Test
    void 이벤트_조회시_이벤트가_존재하지_않으면_예외가_발생한다() {
        // given
        var reader = createMember("reader", "reader@mail.com");
        var eventRead = new EventRead(999L);

        // when // then
        assertThatThrownBy(() -> sut.onEventRead(eventRead))
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

    private OrganizationMember createOrganizationMember(
            Organization organization,
            Member member,
            OrganizationGroup group
    ) {
        var organizationMember =
                OrganizationMember.create("surf", member, organization, OrganizationMemberRole.USER, group);
        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(OrganizationMember organizer, Organization organization) {
        var now = LocalDateTime.now();
        var period = EventOperationPeriod.create(now, now.plusDays(1), now.plusDays(2), now.plusDays(3), now);
        var event = Event.create("title", "description", "place", organizer, organization, period, 100, false);

        return eventRepository.save(event);
    }

    private OrganizationGroup createGroup() {
        return organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
    }
}
