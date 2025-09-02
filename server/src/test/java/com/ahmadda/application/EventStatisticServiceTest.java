package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import com.ahmadda.domain.EventViewMetric;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class EventStatisticServiceTest {

    @Autowired
    private EventStatisticService sut;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStatisticRepository eventStatisticRepository;

    @Test
    void 이벤트_조회수를_가지고_올_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organization, organizationMember);
        createEventStatistic(event);

        // when // then
        assertSoftly(softly -> {
            List<EventViewMetric> eventStatistics =
                    sut.getEventStatistic(event.getId(), new LoginMember(member.getId()));

            softly.assertThat(eventStatistics)
                    .isEmpty();
        });
    }

    @Test
    void 존재하지_않는_조직원일시_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organization, organizationMember);
        createEventStatistic(event);

        var loginMember = new LoginMember(-1L);

        // when // then
        assertThatThrownBy(() -> sut.getEventStatistic(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 존재하지_않는_이벤트일시_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        createOrganizationMember(organization, member);

        var loginMember = new LoginMember(member.getId());
        var nonExistentEventId = 999L;

        // when // then
        assertThatThrownBy(() -> sut.getEventStatistic(nonExistentEventId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("요청한 이벤트 조회수 정보가 존재하지 않습니다.");
    }

    @Test
    void 존재하지_않는_이벤트_조회수_정보일시_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organization, organizationMember);

        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getEventStatistic(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("요청한 이벤트 조회수 정보가 존재하지 않습니다.");
    }

    private Organization createOrganization() {
        Organization organization = Organization.create("테스트 조직", "테스트 설명", "test-image-url");
        return organizationRepository.save(organization);
    }

    private Member createMember() {
        Member member = Member.create("테스트 사용자", "test@example.com", "testPicture");
        return memberRepository.save(member);
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        OrganizationMember organizationMember = OrganizationMember.create("테스트닉네임", member, organization, Role.USER);
        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(Organization organization, OrganizationMember organizer) {
        LocalDateTime now = LocalDateTime.now();
        EventOperationPeriod operationPeriod = EventOperationPeriod.create(
                now.plusDays(1), // registrationStart
                now.plusDays(2),  // registrationEnd
                now.plusDays(3),  // eventStart
                now.plusDays(5),  // eventEnd
                now               // currentDateTime
        );

        Event event = Event.create(
                "테스트 이벤트",
                "테스트 설명",
                "test-location",
                organizer,
                organization,
                operationPeriod,
                30
        );

        return eventRepository.save(event);
    }

    private void createEventStatistic(Event event) {
        EventStatistic eventStatistic = EventStatistic.create(event);
        eventStatisticRepository.save(eventStatistic);
    }
}
