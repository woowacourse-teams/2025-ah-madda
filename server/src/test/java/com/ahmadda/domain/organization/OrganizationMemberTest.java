package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrganizationMemberTest {

    private Member member;
    private Organization organization;
    private OrganizationMember sut;

    @BeforeEach
    void setUp() {
        member = Member.create("테스트 회원", "test@example.com", "testPicture");
        organization = Organization.create("테스트 이벤트 스페이스", "이벤트 스페이스 설명", "image.png");
        sut = OrganizationMember.create(
                "주최자",
                member,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );
    }

    @Test
    void 게스트로_참여한_이벤트_목록을_조회한다() {
        // given
        var participantMember = Member.create("참여자", "participant@example.com", "testPicture");
        var participant =
                OrganizationMember.create(
                        "참여자 구성원",
                        participantMember,
                        organization,
                        OrganizationMemberRole.USER,
                        OrganizationGroup.create("백엔드")
                );

        var event1 = createEvent("이벤트 1");
        var event2 = createEvent("이벤트 2");
        var event3 = createEvent("이벤트 3");

        Guest.create(event1, participant, event1.getRegistrationStart());
        Guest.create(event3, participant, event3.getRegistrationStart());

        // when
        List<Event> participatedEvents = participant.getParticipatedEvents();

        // then
        assertThat(participatedEvents).hasSize(2);
        assertThat(participatedEvents).extracting(Event::getTitle)
                .containsExactlyInAnyOrder("이벤트 1", "이벤트 3");
    }

    @Test
    void 관리자는_같은_이벤트_스페이스의_구성원_권한을_변경할_수_있다() {
        // given
        var targetMember = Member.create("user-m", "user@example.com", "pic");
        var adminMember = Member.create("admin-m", "admin@example.com", "pic");
        var target = OrganizationMember.create(
                "user",
                targetMember,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );
        var admin = OrganizationMember.create(
                "admin",
                adminMember,
                organization,
                OrganizationMemberRole.ADMIN,
                OrganizationGroup.create("백엔드")
        );

        // when
        admin.changeRolesOf(List.of(target), OrganizationMemberRole.ADMIN);

        // then
        assertThat(target.getRole()).isEqualTo(OrganizationMemberRole.ADMIN);
    }

    @Test
    void 권한_변경시_관리자가_아닌_경우_예외가_발생한다() {
        // given
        var targetMember = Member.create("user-m", "user@example.com", "pic");
        var nonAdminMember = Member.create("non-admin-m", "non-admin@example.com", "pic");
        var target = OrganizationMember.create(
                "user",
                targetMember,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );
        var notAdmin = OrganizationMember.create(
                "notAdmin",
                nonAdminMember,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );

        // when // then
        assertThatThrownBy(() -> notAdmin.changeRolesOf(List.of(target), OrganizationMemberRole.ADMIN))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자만 구성원의 권한을 변경할 수 있습니다.");
    }

    @Test
    void 권한_변경시_다른_이벤트_스페이스의_관리자라면_예외가_발생한다() {
        // given
        var targetMember = Member.create("user-m", "user@example.com", "pic");
        var target = OrganizationMember.create(
                "user",
                targetMember,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );

        var otherOrg = Organization.create("다른 이벤트 스페이스", "desc", "image.png");
        var outsiderAdminMember = Member.create("outsider-admin-m", "outsider-admin@example.com", "pic");
        var outsiderAdmin =
                OrganizationMember.create(
                        "outsider",
                        outsiderAdminMember,
                        otherOrg,
                        OrganizationMemberRole.ADMIN,
                        OrganizationGroup.create("백엔드")
                );

        // when // then
        assertThatThrownBy(() -> outsiderAdmin.changeRolesOf(List.of(target), OrganizationMemberRole.ADMIN))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("같은 이벤트 스페이스에 속한 구성원만 권한을 변경할 수 있습니다.");
    }

    private Event createEvent(String title) {
        var now = LocalDateTime.now();
        return Event.create(
                title, "설명", "장소", sut, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(5),
                        now.plusDays(10), now.plusDays(11),
                        now
                ),
                50
        );
    }
}
