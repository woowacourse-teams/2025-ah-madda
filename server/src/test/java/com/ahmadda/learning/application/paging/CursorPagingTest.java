package com.ahmadda.learning.application.paging;

import com.ahmadda.annotation.LearningTest;
import com.ahmadda.application.EventService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.Reminder;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Disabled
@LearningTest
class CursorPagingTest {

    @Autowired
    private EventService sut;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @MockitoSpyBean
    private Reminder reminder;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 동일한_날짜의_과거_이벤트_목록을_두_번째_페이지_조회할_때_올바른_이벤트_목록을_반환한다() {
        // given
        var member = createMember();
        var organization = createOrganization("우테코");
        var group = createGroup();
        var organizationMember = createOrganizationMember(organization, member, group);
        var loginMember = createLoginMember(member);

        var now = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MICROS);

        var createdEvents = new ArrayList<Event>();
        for (int i = 0; i < 15; i++) {
            var event = createEventWithDates(
                    organizationMember,
                    organization,
                    now.minusDays(10)
                            .plusHours(i),
                    now.minusDays(5)
                            .plusHours(i),
                    now.minusDays(2)
                            .plusHours(i),
                    now.minusDays(20)
            );
            createdEvents.add(event);
        }

        // when
        var firstPage = sut.getPastEvents(
                organization.getId(),
                loginMember,
                now,
                Long.MAX_VALUE,
                10
        );

        var lastEventOfFirstPage = firstPage.getLast();

        var secondPage = sut.getPastEvents(
                organization.getId(),
                loginMember,
                now,
                lastEventOfFirstPage.getId(),
                10
        );

        // then
        assertSoftly(softly -> {
            softly.assertThat(firstPage)
                    .hasSize(10);
            softly.assertThat(lastEventOfFirstPage.getId())
                    .isEqualTo(createdEvents.get(5)
                            .getId());

            softly.assertThat(secondPage)
                    .hasSize(5);
            softly.assertThat(secondPage.getFirst()
                            .getId())
                    .isEqualTo(createdEvents.get(4)
                            .getId());
            softly.assertThat(secondPage.getLast()
                            .getId())
                    .isEqualTo(createdEvents.get(0)
                            .getId());

            var firstPageIds = firstPage.stream()
                    .map(Event::getId)
                    .toList();

            softly.assertThat(secondPage)
                    .extracting(Event::getId)
                    .doesNotContainAnyElementsOf(firstPageIds);
        });
    }

    private Organization createOrganization(String name) {
        var organization = Organization.create(name, "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private LoginMember createLoginMember(Member member) {
        return new LoginMember(member.getId());
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

    private Event createEventWithDates(
            final OrganizationMember organizationMember,
            final Organization organization,
            final LocalDateTime registrationEnd,
            final LocalDateTime eventStart,
            final LocalDateTime eventEnd,
            final LocalDateTime now
    ) {
        var event = Event.create(
                "title",
                "description",
                "place",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        registrationEnd.minusDays(1),
                        registrationEnd,
                        eventStart,
                        eventEnd,
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
