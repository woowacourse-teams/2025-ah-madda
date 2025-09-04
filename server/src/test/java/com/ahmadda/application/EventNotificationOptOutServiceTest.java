package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.notification.EventNotificationOptOut;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class EventNotificationOptOutServiceTest {

    @Autowired
    private EventNotificationOptOutService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private EventNotificationOptOutRepository optOutRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void 이벤트에_대한_알림_수신_거부를_설정할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember("user", "user@mail.com");
        var organizationMember = createOrganizationMember("닉네임", member, organization);
        var event = createEvent(organizationMember, organization);
        var loginMember = new LoginMember(member.getId());

        // when
        var saved = sut.optOut(event.getId(), loginMember);

        // then
        assertThat(optOutRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(savedOptOut -> {
                    assertSoftly(softly -> {
                        softly.assertThat(savedOptOut.getEvent())
                                .isEqualTo(event);
                        softly.assertThat(savedOptOut.getOrganizationMember())
                                .isEqualTo(organizationMember);
                    });
                });
    }

    @Test
    void 이벤트가_없으면_수신거부_설정시_예외가_발생한다() {
        // given
        var member = createMember("user", "user@mail.com");
        var loginMember = new LoginMember(member.getId());
        var nonExistentEventId = Long.MAX_VALUE;

        // when // then
        assertThatThrownBy(() -> sut.optOut(nonExistentEventId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 조직의_구성원이_아니면_수신거부_설정시_예외가_발생한다() {
        // given
        var org = createOrganization();
        var event = createEvent(
                createOrganizationMember("닉네임", createMember("user", "user@mail.com"), org),
                org
        );
        var nonExistentMemberId = Long.MAX_VALUE;
        var loginMember = new LoginMember(nonExistentMemberId);

        // when // then
        assertThatThrownBy(() -> sut.optOut(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 이미_수신거부가_설정된_경우_예외가_발생한다() {
        // given
        var org = createOrganization();
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, org);
        var event = createEvent(orgMember, org);

        var loginMember = new LoginMember(member.getId());
        sut.optOut(event.getId(), loginMember);

        // when // then
        assertThatThrownBy(() -> sut.optOut(event.getId(), loginMember))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("이미 해당 이벤트에 대한 알림 수신 거부가 설정되어 있습니다.");
    }

    @Test
    void 알림_수신거부를_취소할_수_있다() {
        // given
        var org = createOrganization();
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, org);
        var event = createEvent(orgMember, org);

        var loginMember = new LoginMember(member.getId());
        sut.optOut(event.getId(), loginMember);

        // when
        sut.cancelOptOut(event.getId(), loginMember);

        // then
        assertThat(optOutRepository.existsByEventAndOrganizationMember(event, orgMember)).isFalse();
    }

    @Test
    void 이벤트가_없으면_수신거부_취소시_예외가_발생한다() {
        // given
        var member = createMember("user", "user@mail.com");
        var loginMember = new LoginMember(member.getId());
        var nonExistentEventId = Long.MAX_VALUE;

        // when // then
        assertThatThrownBy(() -> sut.cancelOptOut(nonExistentEventId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 조직의_구성원이_아니면_수신거부_취소시_예외가_발생한다() {
        // given
        var org = createOrganization();
        var event = createEvent(
                createOrganizationMember("닉네임", createMember("user", "user@mail.com"), org),
                org
        );
        var nonExistentMemberId = Long.MAX_VALUE;
        var loginMember = new LoginMember(nonExistentMemberId);

        // when // then
        assertThatThrownBy(() -> sut.cancelOptOut(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 수신거부_정보가_없으면_취소시_예외가_발생한다() {
        // given
        var org = createOrganization();
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, org);
        var event = createEvent(orgMember, org);

        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.cancelOptOut(event.getId(), loginMember))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("수신 거부 설정이 존재하지 않습니다.");
    }

    @Test
    void 특정_이벤트에_대한_수신_거부_여부_정보를_조회할_수_있다() {
        // given
        var organization = createOrganization();

        var organizer = createOrganizationMember("주최자", createMember("host", "host@mail.com"), organization);
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, organization);

        var event = createEvent(organizer, organization);
        var loginMember = new LoginMember(member.getId());

        optOutRepository.save(EventNotificationOptOut.create(orgMember, event));

        // when
        var result = sut.getMemberWithOptStatus(event.getId(), loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getOrganizationMember())
                    .isEqualTo(orgMember);
            softly.assertThat(result.isOptedOut())
                    .isTrue();
        });
    }

    @Test
    void 수신_거부_여부_정보를_조회시_존재하지_않는_이벤트이면_예외가_발생한다() {
        // given
        var member = createMember("user", "user@mail.com");
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getMemberWithOptStatus(Long.MAX_VALUE, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 수신_거부_여부_정보를_조회시_조직의_구성원이_아니면_예외가_발생한다() {
        // given
        var org = createOrganization();
        var event = createEvent(
                createOrganizationMember("닉네임", createMember("user", "user@mail.com"), org),
                org
        );
        var loginMember = new LoginMember(Long.MAX_VALUE);

        // when // then
        assertThatThrownBy(() -> sut.getMemberWithOptStatus(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 게스트_목록에_알람_수신_거부_정보를_매핑할_수_있다() {
        // given
        var organization = createOrganization();
        var member1 = createMember("user1", "user1@mail.com");
        var member2 = createMember("user2", "user2@mail.com");
        var member3 = createMember("user3", "user3@mail.com");

        var organizer = createOrganizationMember("닉네임1", member1, organization);
        var orgMember1 = createOrganizationMember("닉네임2", member2, organization);
        var orgMember2 = createOrganizationMember("닉네임3", member3, organization);

        var event = createEvent(organizer, organization);

        var guest1 = guestRepository.save(Guest.create(
                event,
                orgMember1,
                LocalDateTime.now()
                        .minusDays(2)
        ));
        var guest2 = guestRepository.save(Guest.create(
                event,
                orgMember2,
                LocalDateTime.now()
                        .minusDays(2)
        ));
        eventRepository.save(event);

        optOutRepository.save(EventNotificationOptOut.create(orgMember2, event));

        // when
        var results = sut.mapGuests(List.of(guest1, guest2));

        // then
        assertSoftly(softly -> {
            softly.assertThat(results)
                    .hasSize(2);

            softly.assertThat(results.get(0)
                                      .getGuest())
                    .isEqualTo(guest1);
            softly.assertThat(results.get(0)
                                      .isOptedOut())
                    .isFalse();

            softly.assertThat(results.get(1)
                                      .getGuest())
                    .isEqualTo(guest2);
            softly.assertThat(results.get(1)
                                      .isOptedOut())
                    .isTrue();
        });
    }

    @Test
    void 조직원_목록에_알람_수신_거부_정보를_매핑할_수_있다() {
        // given
        var organization = createOrganization();
        var member1 = createMember("user1", "user1@mail.com");
        var member2 = createMember("user2", "user2@mail.com");

        var orgMember1 = createOrganizationMember("닉네임1", member1, organization);
        var orgMember2 = createOrganizationMember("닉네임2", member2, organization);

        var event = createEvent(orgMember1, organization);

        optOutRepository.save(EventNotificationOptOut.create(orgMember2, event));

        // when
        var results = sut.mapOrganizationMembers(event.getId(), List.of(orgMember1, orgMember2));

        // then
        assertSoftly(softly -> {
            softly.assertThat(results)
                    .hasSize(2);

            softly.assertThat(results.get(0)
                                      .getOrganizationMember())
                    .isEqualTo(orgMember1);
            softly.assertThat(results.get(0)
                                      .isOptedOut())
                    .isFalse();

            softly.assertThat(results.get(1)
                                      .getOrganizationMember())
                    .isEqualTo(orgMember2);
            softly.assertThat(results.get(1)
                                      .isOptedOut())
                    .isTrue();
        });
    }

    @Test
    void 이벤트가_없으면_조직원_수신거부_정보_매핑시_예외가_발생한다() {
        // given
        var member = createMember("user", "user@mail.com");
        var org = createOrganization();
        var orgMember = createOrganizationMember("닉네임", member, org);
        var nonExistentEventId = Long.MAX_VALUE;

        // when // then
        assertThatThrownBy(() -> sut.mapOrganizationMembers(nonExistentEventId, List.of(orgMember)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    private Organization createOrganization() {
        return organizationRepository.save(Organization.create("조직", "설명", "image.png"));
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "picture"));
    }

    private OrganizationMember createOrganizationMember(String nickname, Member member, Organization org) {
        return organizationMemberRepository.save(OrganizationMember.create(nickname,
                                                                           member,
                                                                           org,
                                                                           OrganizationMemberRole.USER
        ));
    }

    private Event createEvent(OrganizationMember organizer, Organization organization) {
        var now = LocalDateTime.now();

        return eventRepository.save(Event.create(
                "이벤트 제목",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(3),
                        now.minusDays(1),
                        now.plusDays(1),
                        now.plusDays(2),
                        now.minusDays(5)
                ),
                100
        ));
    }
}
