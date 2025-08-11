package com.ahmadda.application;

import com.ahmadda.application.dto.EventRead;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventReadListenerTest {

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

    @Test
    void 해당_이벤트에_대한_통계가_존재하면_조회수가_1_증가한다() {
        // given
        var organization = createOrganization();
        var organizer = createMember("organizer", "organizer@mail.com");
        var reader = createMember("reader", "reader@mail.com");
        var organizationMember = createOrganizationMember(organization, organizer);
        var event = createEvent(organizationMember, organization);
        eventStatisticRepository.save(EventStatistic.create(event));

        var eventRead = new EventRead(event.getId(), new LoginMember(reader.getId()));

        // when
        sut.onEventReaded(eventRead);

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
        var reader = createMember("reader", "reader@mail.com");
        var organizationMember = createOrganizationMember(organization, organizer);
        var event = createEvent(organizationMember, organization);

        var eventRead = new EventRead(event.getId(), new LoginMember(reader.getId()));

        // when
        sut.onEventReaded(eventRead);

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
    void 이벤트_조회시_회원이_존재하지_않으면_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var organizer = createMember("organizer", "organizer@mail.com");
        var organizationMember = createOrganizationMember(organization, organizer);
        var event = createEvent(organizationMember, organization);

        var eventRead = new EventRead(event.getId(), new LoginMember(999L));

        // when // then
        assertThatThrownBy(() -> sut.onEventReaded(eventRead))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_조회시_이벤트가_존재하지_않으면_예외가_발생한다() {
        // given
        var reader = createMember("reader", "reader@mail.com");
        var eventRead = new EventRead(999L, new LoginMember(reader.getId()));

        // when // then
        assertThatThrownBy(() -> sut.onEventReaded(eventRead))
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
        var organizationMember = OrganizationMember.create("surf", member, organization);
        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(OrganizationMember organizer, Organization organization) {
        var now = LocalDateTime.now();
        var period = EventOperationPeriod.create(now, now.plusDays(1), now.plusDays(2), now.plusDays(3), now);
        var event = Event.create("title", "description", "place", organizer, organization, period, 100);

        return eventRepository.save(event);
    }
}
