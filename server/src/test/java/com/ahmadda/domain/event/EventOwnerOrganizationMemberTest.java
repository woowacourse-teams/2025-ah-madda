package com.ahmadda.domain.event;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventOwnerOrganizationMemberTest {

    @Test
    void 공동_주최자를_생성할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization);
        var coOwnerMember = createOrganizationMember(createMember("공동주최자", "coowner@email.com"), organization);

        // when
        var sut = EventOwnerOrganizationMember.create(event, coOwnerMember);

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
        var event = createEvent(organizationMember, organization);

        var anotherOrganization = createOrganization("다른 이벤트 스페이스");
        var memberInAnotherOrg = createOrganizationMember(createMember(), anotherOrganization);

        // when // then
        assertThatThrownBy(() -> EventOwnerOrganizationMember.create(event, memberInAnotherOrg))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("주최자 혹은 공동 주최자는 동일한 이벤트 스페이스에 속해야 합니다.");
    }

    @Test
    void 공동_주최자가_같은_이벤트_스페이스에_속해있는지_확인할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(member, organization);
        var event = createEvent(organizationMember, organization);
        var sut = EventOwnerOrganizationMember.create(event, organizationMember);
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
        var event = createEvent(organizationMember, organization);
        var sut = EventOwnerOrganizationMember.create(event, organizationMember);
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
        var event = createEvent(organizationMember, organization);
        var sut = EventOwnerOrganizationMember.create(event, organizationMember);
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
        return OrganizationMember.create("테스트 닉네임", member, organization, OrganizationMemberRole.USER);
    }

    private Event createEvent(OrganizationMember organizer, Organization organization) {
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
                10
        );
    }
}
