package com.ahmadda.domain.event;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventOrganizerTest {

    @Test
    void 공동_주최자를_생성할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization, false, 10);
        var coOwnerMember = createOrganizationMember(createMember("공동주최자", "coowner@email.com"), organization);

        // when
        var sut = EventOrganizer.create(event, coOwnerMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(sut.getEvent())
                    .isEqualTo(event);
            softly.assertThat(sut.getOrganizationMember())
                    .isEqualTo(coOwnerMember);
        });
    }

    @Test
    void 이벤트와_구성원이_다른_이벤트_스페이스에_속해있으면_공동_주최자_생성시_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization, false, 10);

        var anotherOrganization = createOrganization("다른 이벤트 스페이스");
        var memberInAnotherOrg = createOrganizationMember(createMember(), anotherOrganization);

        // when // then
        assertThatThrownBy(() -> EventOrganizer.create(event, memberInAnotherOrg))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("주최자는 동일한 이벤트 스페이스에 속해야 합니다.");
    }

    @Test
    void 공동_주최자가_같은_이벤트_스페이스에_속해있는지_확인할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization, false, 10);
        var sut = EventOrganizer.create(event, organizationMember);
        var anotherOrganization = createOrganization("다른 이벤트 스페이스");

        // when
        var actual1 = sut.isBelongTo(organization);
        var actual2 = sut.isBelongTo(anotherOrganization);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isTrue();
            softly.assertThat(actual2)
                    .isFalse();
        });
    }

    @Test
    void 공동_주최자가_같은_회원인지_확인할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization, false, 10);
        var sut = EventOrganizer.create(event, organizationMember);
        var anotherMember = createMember("다른 회원", "another@email.com");

        // when
        var actual1 = sut.isSameMember(member);
        var actual2 = sut.isSameMember(anotherMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isTrue();
            softly.assertThat(actual2)
                    .isFalse();
        });
    }

    @Test
    void 공동_주최자가_같은_이벤트_스페이스_구성원인지_확인할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization, false, 10);
        var sut = EventOrganizer.create(event, organizationMember);
        var anotherOrganizationMember = createOrganizationMember(createMember(), organization);

        // when
        var actual1 = sut.isSameOrganizationMember(organizationMember);
        var actual2 = sut.isSameOrganizationMember(anotherOrganizationMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isTrue();
            softly.assertThat(actual2)
                    .isFalse();
        });
    }

    @Test
    void 승인_이벤트에서_주최자는_게스트를_승인할_수_있다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var event = createEvent(organizationMember1, organization, true, 10);
        var eventOrganizer = event.getEventOrganizers()
                .getFirst();
        var guest = Guest.create(event, organizationMember2, event.getRegistrationStart());

        //when
        eventOrganizer.approve(guest);

        //then
        assertThat(guest.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }

    @Test
    void 주최자는_게스트를_승인할때_주최자의_이벤트에_해당하는_게스트가_아니라면_예외가_발생한다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var event1 = createEvent(organizationMember1, organization, true, 10);
        var event2 = createEvent(organizationMember1, organization, true, 10);
        var eventOrganizer = event1.getEventOrganizers()
                .getFirst();
        var guest = Guest.create(event2, organizationMember2, event2.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> eventOrganizer.approve(guest))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("해당 이벤트의 게스트가 아닙니다.");
    }

    @Test
    void 승인_이벤트가_아닐때_주최자가_게스트를_승인하면_예외가_발생한다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var event = createEvent(organizationMember1, organization, false, 10);
        var eventOrganizer = event.getEventOrganizers()
                .getFirst();
        var guest = Guest.create(event, organizationMember2, event.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> eventOrganizer.approve(guest))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("승인 가능한 이벤트가 아니라 승인 상태를 변경할 수 없습니다.");
    }

    @Test
    void 승인_이벤트의_수용인원이_가득찼을때_주최자가_게스트를_승인하면_예외가_발생한다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var member3 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var organizationMember3 = createOrganizationMember(member3, organization);
        var event = createEvent(organizationMember1, organization, true, 1);
        var eventOrganizer = event.getEventOrganizers()
                .getFirst();
        var guest1 = Guest.create(event, organizationMember2, event.getRegistrationStart());
        eventOrganizer.approve(guest1);

        var guest2 = Guest.create(event, organizationMember3, event.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> eventOrganizer.approve(guest2))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("수용 인원이 가득차 해당 게스트를 승인할 수 없습니다.");
    }

    @Test
    void 승인_이벤트에서_주최자는_게스트를_거절할_수_있다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var event = createEvent(organizationMember1, organization, true, 10);
        var eventOrganizer = event.getEventOrganizers()
                .getFirst();
        var guest = Guest.create(event, organizationMember2, event.getRegistrationStart());

        //when
        eventOrganizer.reject(guest);

        //then
        assertThat(guest.getApprovalStatus()).isEqualTo(ApprovalStatus.REJECTED);
    }

    @Test
    void 승인_이벤트가_아닐때_주최자가_게스트를_거절하면_예외가_발생한다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var event = createEvent(organizationMember1, organization, false, 10);
        var eventOrganizer = event.getEventOrganizers()
                .getFirst();
        var guest = Guest.create(event, organizationMember2, event.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> eventOrganizer.reject(guest))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("승인 가능한 이벤트가 아니라 승인 상태를 변경할 수 없습니다.");
    }

    @Test
    void 주최자는_게스트를_거절할때_주최자의_이벤트에_해당하는_게스트가_아니라면_예외가_발생한다() {
        //given
        var organization = createOrganization();
        var member1 = createMember();
        var member2 = createMember();
        var organizationMember1 = createOrganizationMember(member1, organization);
        var organizationMember2 = createOrganizationMember(member2, organization);
        var event1 = createEvent(organizationMember1, organization, true, 10);
        var event2 = createEvent(organizationMember1, organization, true, 10);
        var eventOrganizer = event1.getEventOrganizers()
                .getFirst();
        var guest = Guest.create(event2, organizationMember2, event2.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> eventOrganizer.reject(guest))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("해당 이벤트의 게스트가 아닙니다.");
    }

    private Member createMember() {
        return Member.create("테스트 멤버", "test@email.com", "testPicture");
    }

    private Member createMember(String name, String email) {
        return Member.create(name, email, "testPicture");
    }

    private Organization createOrganization() {
        return Organization.create("테스트 이벤트 스페이스", "설명", "image.png");
    }

    private Organization createOrganization(String name) {
        return Organization.create(name, "설명", "image.png");
    }

    private OrganizationMember createOrganizationMember(Member member, Organization organization) {
        return OrganizationMember.create(
                "테스트 닉네임",
                member,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );
    }

    private Event createEvent(
            OrganizationMember organizer,
            Organization organization,
            boolean isApprovalRequired,
            int maxCapacity
    ) {
        var now = LocalDateTime.now();
        return Event.create(
                "테스트 이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                maxCapacity,
                isApprovalRequired
        );
    }
}
