package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatistic;
import com.ahmadda.domain.event.EventStatisticRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void 주최자는_이벤트_조회수를_가지고_올_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organization, organizationMember);
        createEventStatistic(event);

        //when
        var eventStatistics = sut.getEventStatistic(event.getId(), new LoginMember(member.getId()));

        // then
        assertThat(eventStatistics).isEmpty();
    }

    @Test
    void 주최자에_속하지_않으면_이벤트_조회수를_요청시_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var nonCreateMember = createMember("test", "test@naver.com");
        var organizationMember = createOrganizationMember(organization, member);
        var nonCreateOrganizationMember = createOrganizationMember(organization, nonCreateMember);
        var event = createEvent(organization, organizationMember);
        createEventStatistic(event);

        // when //then
        assertThatThrownBy(() -> sut.getEventStatistic(event.getId(), new LoginMember(nonCreateMember.getId())))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트의 조회수는 이벤트의 주최자만 조회할 수 있습니다.");
    }

    @Test
    void 공동_주최자는_이벤트_조회수를_가져올_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var otherCreateMember = createMember("test", "test@naver.com");
        var organizationMember = createOrganizationMember(organization, member);
        var otherCreateOrganizationMember = createOrganizationMember(organization, otherCreateMember);
        var event = createEvent(organization, organizationMember, List.of(otherCreateOrganizationMember.getId()));
        createEventStatistic(event);

        //when
        var eventStatistics = sut.getEventStatistic(event.getId(), new LoginMember(otherCreateMember.getId()));

        // when //then
        assertThat(eventStatistics).isEmpty();
    }


    @Test
    void 존재하지_않는_구성원일시_예외가_발생한다() {
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
                .hasMessage("존재하지 않는 구성원입니다.");
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
                .hasMessage("존재하지 않는 요청한 이벤트 조회수 정보입니다.");
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
                .hasMessage("존재하지 않는 요청한 이벤트 조회수 정보입니다.");
    }

    private Organization createOrganization() {
        Organization organization = Organization.create("테스트 이벤트 스페이스", "테스트 설명", "test-image-url");
        return organizationRepository.save(organization);
    }

    private Member createMember() {
        Member member = Member.create("테스트 사용자", "test@example.com", "testPicture");
        return memberRepository.save(member);
    }

    private Member createMember(String name, String email) {
        Member member = Member.create(name, email, "testPicture");
        return memberRepository.save(member);
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        OrganizationMember organizationMember =
                OrganizationMember.create("테스트닉네임", member, organization, OrganizationMemberRole.USER);
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

    private Event createEvent(
            Organization organization,
            OrganizationMember organizationMember,
            List<Long> organizationMemberIds
    ) {
        LocalDateTime now = LocalDateTime.now();
        EventOperationPeriod operationPeriod = EventOperationPeriod.create(
                now.plusDays(1), // registrationStart
                now.plusDays(2),  // registrationEnd
                now.plusDays(3),  // eventStart
                now.plusDays(5),  // eventEnd
                now               // currentDateTime
        );

        List<OrganizationMember> organizationMembers = organizationMemberIds.stream()
                .map(organizationMemberId -> organizationMemberRepository.getReferenceById(organizationMemberId))
                .toList();

        Event event = Event.create(
                "테스트 이벤트",
                "테스트 설명",
                "test-location",
                organizationMember,
                organization,
                operationPeriod,
                30,
                organizationMembers
        );

        return eventRepository.save(event);
    }

    private void createEventStatistic(Event event) {
        EventStatistic eventStatistic = EventStatistic.create(event);
        eventStatisticRepository.save(eventStatistic);
    }
}
