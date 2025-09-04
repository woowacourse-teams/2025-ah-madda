package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationMemberRoleUpdateRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class OrganizationMemberServiceTest {

    @Autowired
    private OrganizationMemberService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Test
    void 자신의_조직원_정보를_조회한다() {
        // given
        var org = createOrganization("우테코");
        var member = createMember("홍길동", "hong@email.com");
        var orgMember = createOrganizationMember("닉네임", member, org, OrganizationMemberRole.USER);
        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.getOrganizationMember(org.getId(), loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getId())
                    .isEqualTo(orgMember.getId());
            softly.assertThat(result.getMember()
                            .getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getOrganization()
                            .getId())
                    .isEqualTo(org.getId());
        });
    }

    @Test
    void 자신의_조직원_정보_조회시_존재하지_않는_조직원이면_예외가_발생한다() {
        // given
        Organization org = createOrganization("우테코");
        Member member = createMember("홍길동", "hong@email.com");
        LoginMember loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getOrganizationMember(org.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 조직원_역할을_한번에_여러명_변경할_수_있다() {
        // given
        var org = createOrganization("아맞다");
        var admin = createMember("admin", "admin@email.com");
        var user1 = createMember("user1", "user1@email.com");
        var user2 = createMember("user2", "user2@email.com");

        var adminOrgMember =
                createOrganizationMember("admin", admin, org, OrganizationMemberRole.ADMIN);
        var user1OrgMember =
                createOrganizationMember("user1", user1, org, OrganizationMemberRole.USER);
        var user2OrgMember =
                createOrganizationMember("user2", user2, org, OrganizationMemberRole.USER);

        var request = new OrganizationMemberRoleUpdateRequest(
                List.of(user1OrgMember.getId(), user2OrgMember.getId()),
                OrganizationMemberRole.ADMIN
        );
        var loginMember = new LoginMember(admin.getId());

        // when
        sut.updateRoles(loginMember, request);

        // then
        var updatedUser1 = organizationMemberRepository.findById(user1OrgMember.getId())
                .orElseThrow();
        var updatedUser2 = organizationMemberRepository.findById(user2OrgMember.getId())
                .orElseThrow();

        assertSoftly(softly -> {
            softly.assertThat(updatedUser1.getRole())
                    .isEqualTo(OrganizationMemberRole.ADMIN);
            softly.assertThat(updatedUser2.getRole())
                    .isEqualTo(OrganizationMemberRole.ADMIN);
        });
    }

    @Test
    void 조직원_역할_변경시_대상_중_일부라도_존재하지_않으면_예외가_발생한다() {
        // given
        var org = createOrganization("아맞다");
        var admin = createMember("admin", "admin@email.com");
        var user = createMember("user", "user@email.com");

        var adminOrgMember =
                createOrganizationMember("admin", admin, org, OrganizationMemberRole.ADMIN);
        var userOrgMember =
                createOrganizationMember("user", user, org, OrganizationMemberRole.USER);

        var invalidId = -999L;
        var request = new OrganizationMemberRoleUpdateRequest(
                List.of(userOrgMember.getId(), invalidId),
                OrganizationMemberRole.ADMIN
        );
        var loginMember = new LoginMember(admin.getId());

        // when // then
        assertThatThrownBy(() -> sut.updateRoles(loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("일부 조직원이 존재하지 않습니다.");
    }

    @Test
    void 조직원_역할_변경시_대상_조직원이_다른_조직에_속해있다면_예외가_발생한다() {
        // given
        var org1 = createOrganization("아맞다1");
        var org2 = createOrganization("아맞다2");
        var admin = createMember("admin", "admin@email.com");
        var user1 = createMember("user1", "user1@email.com");
        var user2 = createMember("user2", "user2@email.com");

        var adminOrgMember =
                createOrganizationMember("admin", admin, org1, OrganizationMemberRole.ADMIN);
        var user1OrgMember =
                createOrganizationMember("user1", user1, org1, OrganizationMemberRole.USER);
        var user2OrgMember =
                createOrganizationMember("user2", user2, org2, OrganizationMemberRole.USER);

        var request = new OrganizationMemberRoleUpdateRequest(
                List.of(user1OrgMember.getId(), user2OrgMember.getId()),
                OrganizationMemberRole.ADMIN
        );
        var loginMember = new LoginMember(admin.getId());

        // when // then
        assertThatThrownBy(() -> sut.updateRoles(loginMember, request))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("모든 대상은 같은 조직에 속해 있어야 합니다.");
    }

    @Test
    void 조직_멤버_목록을_조회할_수_있다() {
        // given
        var org = createOrganization("우테코");
        var member1 = createMember("홍길동", "hong@email.com");
        var member2 = createMember("박찬호", "chanho@email.com");

        createOrganizationMember("길동", member1, org, OrganizationMemberRole.USER);
        createOrganizationMember("찬호", member2, org, OrganizationMemberRole.USER);

        var loginMember = new LoginMember(member1.getId());

        // when
        var result = sut.getAllOrganizationMembers(org.getId(), loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .extracting(OrganizationMember::getMember)
                    .extracting(Member::getId)
                    .containsExactlyInAnyOrder(member1.getId(), member2.getId());
        });
    }

    @Test
    void 조직_멤버_목록_조회시_조직이_존재하지_않으면_예외가_발생한다() {
        // given
        var member = createMember("홍길동", "hong@email.com");
        var loginMember = new LoginMember(member.getId());

        var invalidOrgId = -999L;

        // when // then
        assertThatThrownBy(() -> sut.getAllOrganizationMembers(invalidOrgId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직입니다.");
    }
    
    @Test
    void 조직_멤버_목록_조회시_조직에_속하지_않은_조직원이면_예외가_발생한다() {
        // given
        var org1 = createOrganization("우테코");
        var org2 = createOrganization("다른조직");

        var member = createMember("홍길동", "hong@email.com");
        createOrganizationMember("길동", member, org2, OrganizationMemberRole.USER); // 다른 조직 소속

        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getAllOrganizationMembers(org1.getId(), loginMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("조직에 속한 조직원만 조직원의 목록을 조회할 수 있습니다.");
    }

    private Organization createOrganization(String name) {
        return organizationRepository.save(Organization.create(name, "설명", "img.png"));
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "pic.png"));
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            Member member,
            Organization org,
            OrganizationMemberRole role
    ) {
        return organizationMemberRepository.save(
                OrganizationMember.create(nickname, member, org, role)
        );
    }
}
