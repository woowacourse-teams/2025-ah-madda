package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class OrganizationTest {

    private Organization sut;
    private OrganizationMember organizer;

    @BeforeEach
    void setUp() {
        sut = Organization.create("테스트 조직", "조직 설명", "image.png");
        var member = Member.create("주최자 회원", "organizer@example.com", "testPicture");
        organizer = OrganizationMember.create("주최자", member, sut, OrganizationMemberRole.USER);
    }

    @Test
    void 활성화된_이벤트_목록을_조회한다() {
        // given
        var now = LocalDateTime.now();
        var pastEvent = createEventForTest(
                "과거 이벤트",
                now.minusDays(3), now.minusDays(2),
                now.minusDays(1), now.plusDays(1)
        );
        var activeEvent1 = createEventForTest(
                "활성 이벤트 1",
                now.minusDays(1), now.plusDays(1),
                now.plusDays(2), now.plusDays(3)
        );
        var activeEvent2 = createEventForTest(
                "활성 이벤트 2",
                now.minusDays(1), now.plusDays(1),
                now.plusDays(2), now.plusDays(3)
        );

        // when
        var activeEvents = sut.getActiveEvents(now);

        // then
        assertSoftly(softly -> {
            softly.assertThat(activeEvents)
                    .hasSize(2);
            softly.assertThat(activeEvents)
                    .extracting(Event::getTitle)
                    .containsExactlyInAnyOrder("활성 이벤트 1", "활성 이벤트 2");
        });
    }

    @Test
    void 조직에_참여할_수_있다() {
        //given
        var member = Member.create("주최자 회원", "organizer@example.com", "testPicture");
        var inviteCode = InviteCode.create("code", sut, organizer, LocalDateTime.now());

        //when
        var organizationMember = sut.participate(member, "surf", inviteCode, LocalDateTime.now());

        //then
        assertSoftly(softly -> {
            softly.assertThat(organizationMember.getOrganization())
                    .isEqualTo(sut);
            softly.assertThat(organizationMember.getMember())
                    .isEqualTo(member);
            softly.assertThat(organizationMember.getNickname())
                    .isEqualTo("surf");
        });
    }

    @Test
    void 조직의_초대코드가_아닌_초대코드로_조직에_참여한다면_예외가_발생한다() {
        //given
        var organization = Organization.create("테스트 조직2", "조직 설명", "image.png");
        var member = Member.create("주최자 회원", "organizer@example.com", "testPicture");
        var inviter = OrganizationMember.create("test", member, organization, OrganizationMemberRole.USER);
        var inviteCode = InviteCode.create("code", organization, inviter, LocalDateTime.now());

        //when //then
        assertThatThrownBy(() -> sut.participate(member, "surf", inviteCode, LocalDateTime.now()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("잘못된 초대코드입니다.");
    }

    @Test
    void 만료된_초대코드로_조직에_참여한다면_예외가_발생한다() {
        //given
        var member = Member.create("주최자 회원", "organizer@example.com", "testPicture");
        var inviteCode = InviteCode.create("code", sut, organizer, LocalDateTime.of(2000, 1, 1, 0, 0));

        //when //then
        assertThatThrownBy(() -> sut.participate(member, "surf", inviteCode, LocalDateTime.now()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("초대코드가 만료되었습니다.");
    }

    @Test
    void 관리자가_조직정보를_수정할_수_있다() {
        // given
        var admin = OrganizationMember.create("관리자", organizer.getMember(), sut, OrganizationMemberRole.ADMIN);

        // when
        sut.update(admin, "새 조직명", "새 설명", "newImage.png");

        // then
        assertSoftly(softly -> {
            softly.assertThat(sut.getName())
                    .isEqualTo("새 조직명");
            softly.assertThat(sut.getDescription())
                    .isEqualTo("새 설명");
            softly.assertThat(sut.getImageUrl())
                    .isEqualTo("newImage.png");
        });
    }

    @Test
    void 관리자가_아니면_조직정보를_수정한다면_예외가_발생한다() {
        // given
        var user = OrganizationMember.create("일반회원", organizer.getMember(), sut, OrganizationMemberRole.USER);

        // when // then
        assertThatThrownBy(() ->
                sut.update(user, "새 조직명", "새 설명", "newImage.png")
        )
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("조직원의 관리자만 조직 정보를 수정할 수 있습니다.");
    }

    @Test
    void 다른_조직의_관리자가_조직정보를_수정다면_예외가_발생한다() {
        // given
        var otherOrganization = Organization.create("테스트 조직", "조직 설명", "image.png");
        var organizationMember = OrganizationMember.create(
                "일반회원",
                organizer.getMember(),
                otherOrganization,
                OrganizationMemberRole.USER
        );

        // when // then
        assertThatThrownBy(() ->
                sut.update(organizationMember, "새 조직명", "새 설명", "newImage.png")
        )
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("조직에 속한 조직원만 수정이 가능합니다.");
    }

    private Event createEventForTest(
            String title,
            LocalDateTime registrationStart,
            LocalDateTime registrationEnd,
            LocalDateTime eventStart,
            LocalDateTime eventEnd
    ) {
        return Event.create(
                title, "설명", "장소", organizer, sut,
                EventOperationPeriod.create(
                        registrationStart, registrationEnd,
                        eventStart, eventEnd,
                        registrationStart.minusDays(1)
                ),
                50
        );
    }
}
