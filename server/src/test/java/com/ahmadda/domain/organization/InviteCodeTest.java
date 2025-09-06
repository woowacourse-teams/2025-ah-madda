package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.member.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class InviteCodeTest {

    @Test
    void 이벤트_스페이스에_속한_구성원이_아닌데_초대코드를_만든다면_예외가_발생한다() {
        //given
        var organization1 = createOrganization("우테코");
        var organization2 = createOrganization("아맞다");
        var member = createMember();
        var inviter = createOrganizationMember(member, organization1);

        //when //then
        assertThatThrownBy(() -> InviteCode.create("code", organization2, inviter, LocalDateTime.now()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스에 참여 중인 구성원만 해당 이벤트 스페이스의 초대코드를 만들 수 있습니다.");
    }

    @Test
    void 초대코드를_생성할때_기본_만료날짜는_현재부터_7일_뒤여야_한다() {
        //given
        var organization = createOrganization("우테코");
        var member = createMember();
        var inviter = createOrganizationMember(member, organization);
        var currentDateTime = LocalDateTime.now();

        //when
        var inviteCode = InviteCode.create("code", organization, inviter, currentDateTime);

        //then
        assertThat(inviteCode.getExpiresAt()).isEqualTo(currentDateTime.plusDays(7));
    }

    @Test
    void 초대코드가_만료됐는지_확인할_수_있다() {
        //given
        var organization = createOrganization("우테코");
        var member = createMember();
        var inviter = createOrganizationMember(member, organization);
        var expiredInviteCode = InviteCode.create("code", organization, inviter, LocalDateTime.of(2000, 1, 1, 0, 0));
        var nonExpiredInviteCode = InviteCode.create("code", organization, inviter, LocalDateTime.now());

        //when
        var actual1 = expiredInviteCode.isExpired(LocalDateTime.now());
        var actual2 = nonExpiredInviteCode.isExpired(LocalDateTime.now());

        //then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isEqualTo(true);
            softly.assertThat(actual2)
                    .isEqualTo(false);
        });
    }

    @Test
    void 특정_이벤트_스페이스의_초대코드인지_확인할_수_있다() {
        //given
        var wooteco = createOrganization("우테코");
        var ahmadda = createOrganization("아맞다");
        var member = createMember();
        var inviter = createOrganizationMember(member, wooteco);
        var wootecoInviteCode = InviteCode.create("code", wooteco, inviter, LocalDateTime.of(2000, 1, 1, 0, 0));

        //when
        var actual1 = wootecoInviteCode.matchesOrganization(wooteco);
        var actual2 = wootecoInviteCode.matchesOrganization(ahmadda);

        //then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isEqualTo(true);
            softly.assertThat(actual2)
                    .isEqualTo(false);
        });
    }

    private OrganizationMember createOrganizationMember(Member member, Organization organization) {
        return OrganizationMember.create("nickname", member, organization, OrganizationMemberRole.USER);
    }

    private Member createMember() {
        return Member.create("이재훈", "dlwogns3413@ahamadda.com", "testPicture");
    }

    private Organization createOrganization(String name) {
        return Organization.create(name, "우테코입니다.", "imageUrl");
    }
}
