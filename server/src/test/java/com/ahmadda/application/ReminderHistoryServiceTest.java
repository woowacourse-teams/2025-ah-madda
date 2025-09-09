package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.ReminderHistory;
import com.ahmadda.domain.notification.ReminderHistoryRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class ReminderHistoryServiceTest {

    @Autowired
    private ReminderHistoryService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private ReminderHistoryRepository reminderHistoryRepository;

    @Autowired
    private JwtAccessTokenProperties accessTokenProperties;

    @Autowired
    private JwtRefreshTokenProperties refreshTokenProperties;

    @Test
    void 주최자가_리마인드_히스토리를_조회한다() {
        // given
        var organization = createOrganization();
        var organizerMember = createMember("organizer", "organizer@mail.com");
        var organizer = createOrganizationMember(organization, organizerMember);
        var event = createEvent(organizer, organization);

        reminderHistoryRepository.save(ReminderHistory.createNow(event, "첫 번째 알림입니다.", List.of()));
        reminderHistoryRepository.save(ReminderHistory.createNow(event, "두 번째 알림입니다.", List.of()));

        var loginMember = new LoginMember(organizerMember.getId());

        // when
        var result = sut.getNotifyHistory(event.getId(), loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .extracting(ReminderHistory::getEvent)
                    .containsOnly(event);
            softly.assertThat(result)
                    .extracting(ReminderHistory::getContent)
                    .containsExactlyInAnyOrder("첫 번째 알림입니다.", "두 번째 알림입니다.");
        });
    }

    @Test
    void 존재하지_않는_이벤트로_조회하면_예외가_발생한다() {
        // given
        var member = createMember("m", "m@mail.com");
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getNotifyHistory(999L, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 이벤트_이벤트_스페이스의_구성원이_아니면_예외가_발생한다() {
        // given
        var org1 = createOrganization();
        var org2 = createOrganization();

        var organizerMember = createMember("host", "host@mail.com");
        var outsiderMember = createMember("out", "out@mail.com");

        var organizer = createOrganizationMember(org1, organizerMember);
        createOrganizationMember(org2, outsiderMember);

        var event = createEvent(organizer, org1);

        var loginMember = new LoginMember(outsiderMember.getId());

        // when // then
        assertThatThrownBy(() -> sut.getNotifyHistory(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원 정보입니다.");
    }

    @Test
    void 주최자가_아니면_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var organizerMember = createMember("host", "host@mail.com");
        var normalMember = createMember("user", "user@mail.com");

        var organizer = createOrganizationMember(organization, organizerMember);
        createOrganizationMember(organization, normalMember);

        var event = createEvent(organizer, organization);

        var loginMember = new LoginMember(normalMember.getId());

        // when // then
        assertThatThrownBy(() -> sut.getNotifyHistory(event.getId(), loginMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("리마인더 히스토리는 이벤트의 주최자만 조회할 수 있습니다.");
    }

    private Organization createOrganization() {
        return organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
    }

    private Member createMember(final String name, final String email) {
        return memberRepository.save(Member.create(name, email, "testPicture"));
    }

    private OrganizationMember createOrganizationMember(final Organization organization, final Member member) {
        return organizationMemberRepository.save(OrganizationMember.create(
                "nick",
                member,
                organization,
                OrganizationMemberRole.USER
        ));
    }

    private Event createEvent(final OrganizationMember organizer, final Organization organization) {
        var now = LocalDateTime.now();
        var event = Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(3), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(6)
                ),
                100
        );
        return eventRepository.save(event);
    }
}
