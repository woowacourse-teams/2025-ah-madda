package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestRepository;
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

class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 자신의_회원_정보를_조회한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "hong@gildong.com", "testPicture"));
        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.getMember(loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getName())
                    .isEqualTo("홍길동");
            softly.assertThat(result.getEmail())
                    .isEqualTo("hong@gildong.com");
            softly.assertThat(result.getProfileImageUrl())
                    .isEqualTo("testPicture");
        });
    }

    @Test
    void 존재하지_않는_회원이면_예외가_발생한다() {
        // given
        var loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.getMember(loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 회원이_주최한_이벤트를_조회할_수_있다() {
        // given
        var organization1 = createOrganization("우테코");
        var organization2 = createOrganization("우테코2");
        var member1 = createMember("surf", "surf@mail.com");
        var member2 = createMember("surf2", "surf2@mail.com");
        var group = createGroup();

        var now = LocalDateTime.now();

        var event1 = createEvent(
                createOrganizationMember(organization1, member1, group),
                organization1,
                "event1",
                now.minusDays(2),
                now.plusDays(1)
        );
        var event2 = createEvent(
                createOrganizationMember(organization2, member1, group),
                organization2,
                "event2",
                now.minusDays(3),
                now.minusDays(1)
        );
        var event3 = createEvent(
                createOrganizationMember(organization1, member2, group),
                organization1,
                "event3",
                now.plusDays(1),
                now.plusDays(2)
        );

        var loginMember = new LoginMember(member1.getId());

        // when
        var events = sut.getOwnerEvents(loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(events)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(event1, event2);
        });
    }

    @Test
    void 회원이_참여한_이벤트를_조회할_수_있다() {
        // given
        var organization1 = createOrganization("우테코");
        var organization2 = createOrganization("우테코2");
        var member = createMember("surf", "surf@mail.com");
        var group = createGroup();
        var organizer = createOrganizationMember(organization1, member, group);
        var organizer2 = createOrganizationMember(organization2, member, group);
        var guestMember1 = createMember("guest", "guest@mail.com");
        var guestMember2 = createMember("guest2", "guest2@mail.com");

        var now = LocalDateTime.now();

        var event1 = createEvent(organizer, organization1, "event1", now.minusDays(2), now.plusDays(1));
        var event2 = createEvent(organizer, organization1, "event2", now.minusDays(3), now.minusDays(1));
        var event3 = createEvent(organizer2, organization2, "event3", now.plusDays(1), now.plusDays(2));

        guestRepository.save(Guest.create(
                event1,
                createOrganizationMember(organization1, guestMember1, group),
                event1.getRegistrationStart()
        ));
        guestRepository.save(Guest.create(
                event2,
                createOrganizationMember(organization1, guestMember2, group),
                event2.getRegistrationStart()
        ));
        guestRepository.save(Guest.create(
                event3,
                createOrganizationMember(organization2, guestMember1, group),
                event3.getRegistrationStart()
        ));

        var loginMember = new LoginMember(guestMember1.getId());

        // when
        var participatedEvents = sut.getParticipatedEvents(loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(participatedEvents)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(event1, event3);
        });
    }

    private Organization createOrganization(String name) {
        var organization = Organization.create(name, "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "testPicture"));
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

    private Event createEvent(
            OrganizationMember organizer,
            Organization organization,
            String title,
            LocalDateTime start,
            LocalDateTime end
    ) {

        return eventRepository.save(Event.create(
                title,
                "description",
                "place",
                organizer,
                organization,
                EventOperationPeriod.create(
                        start, end,
                        end.plusHours(1), end.plusHours(2),
                        start.minusDays(1)
                ),
                100,
                false
        ));
    }

    private OrganizationGroup createGroup() {
        return organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
    }
}
