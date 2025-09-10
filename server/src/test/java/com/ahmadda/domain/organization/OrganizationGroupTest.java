package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.member.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrganizationGroupTest {

    @Test
    void 이벤트_스페이스의_어드민은_그룹을_생성할_수_있다() {
        //given
        var organization = Organization.create("우테코", "설명", "url");
        var member = Member.create("서프", "dlwogns3413@gmail.com", "profile");
        var organizationMember =
                OrganizationMember.create("서프", member, organization, OrganizationMemberRole.ADMIN);

        //when
        var group = OrganizationGroup.create("backend", organization, organizationMember);

        //then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(group.getName())
                    .isEqualTo("backend");
            softly.assertThat(group.getOrganization())
                    .isEqualTo(organization);
        });
    }

    @Test
    void 이벤트_스페이스의_어드민이_아니라면_그룹을_생성할때_예외가_발생한다() {
        //given
        var organization = Organization.create("우테코", "설명", "url");
        var member = Member.create("서프", "dlwogns3413@gmail.com", "profile");
        var organizationMember =
                OrganizationMember.create("서프", member, organization, OrganizationMemberRole.USER);

        //when //then
        assertThatThrownBy(() -> OrganizationGroup.create("backend", organization, organizationMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("어드민만 그룹을 만들 수 있습니다.");
    }

    @Test
    void 이벤트_스페이스의_구성원이_아니라면_그룹을_생성할때_예외가_발생한다() {
        //given
        var organization1 = Organization.create("우테코", "설명", "url");
        var organization2 = Organization.create("아맞다", "설명", "url");
        var member = Member.create("서프", "dlwogns3413@gmail.com", "profile");
        var organizationMember =
                OrganizationMember.create("서프", member, organization2, OrganizationMemberRole.USER);

        //when //then
        assertThatThrownBy(() -> OrganizationGroup.create("backend", organization1, organizationMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스의 구성원만 그룹을 만들 수 있습니다.");
    }
}
