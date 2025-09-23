package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationMemberRoleUpdateRequest;
import com.ahmadda.application.dto.OrganizationMemberUpdateRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 자신의_구성원_정보를_조회한다() {
        // given
        var org = createOrganization("우테코");
        var member = createMember("홍길동", "hong@email.com");
        var group = createGroup("백엔드");
        var orgMember = createOrganizationMember("닉네임", member, org, OrganizationMemberRole.USER, group);
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
    void 자신의_구성원_정보_조회시_존재하지_않는_구성원이면_예외가_발생한다() {
        // given
        Organization org = createOrganization("우테코");
        Member member = createMember("홍길동", "hong@email.com");
        LoginMember loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getOrganizationMember(org.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 구성원_역할을_한번에_여러명_변경할_수_있다() {
        var org = createOrganization("아맞다");
        var admin = createMember("admin", "admin@email.com");
        var user1 = createMember("user1", "user1@email.com");
        var user2 = createMember("user2", "user2@email.com");

        var group = createGroup("백엔드");
        var adminOrgMember = createOrganizationMember("admin", admin, org, OrganizationMemberRole.ADMIN, group);
        var user1OrgMember = createOrganizationMember("user1", user1, org, OrganizationMemberRole.USER, group);
        var user2OrgMember = createOrganizationMember("user2", user2, org, OrganizationMemberRole.USER, group);

        var request = new OrganizationMemberRoleUpdateRequest(
                List.of(user1OrgMember.getId(), user2OrgMember.getId()),
                OrganizationMemberRole.ADMIN
        );
        var loginMember = new LoginMember(admin.getId());

        sut.updateRoles(org.getId(), loginMember, request);

        var updated1 = organizationMemberRepository.findById(user1OrgMember.getId())
                .orElseThrow();
        var updated2 = organizationMemberRepository.findById(user2OrgMember.getId())
                .orElseThrow();

        assertSoftly(softly -> {
            softly.assertThat(updated1.getRole())
                    .isEqualTo(OrganizationMemberRole.ADMIN);
            softly.assertThat(updated2.getRole())
                    .isEqualTo(OrganizationMemberRole.ADMIN);
        });
    }

    @Test
    void 구성원_역할_변경시_존재하지_않는_이벤트_스페이스가면_예외가_발생한다() {
        var admin = createMember("admin", "admin@email.com");
        var loginMember = new LoginMember(admin.getId());
        var request = new OrganizationMemberRoleUpdateRequest(List.of(1L, 2L), OrganizationMemberRole.ADMIN);

        assertThatThrownBy(() -> sut.updateRoles(-1L, loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스입니다.");
    }

    @Test
    void 구성원_역할_변경시_요청자가_이벤트_스페이스에_속하지_않으면_예외가_발생한다() {
        var org = createOrganization("아맞다");
        var outsider = createMember("outsider", "out@email.com");
        var user = createMember("user", "user@email.com");

        var group = createGroup("백엔드");
        var userOrgMember = createOrganizationMember("user", user, org, OrganizationMemberRole.USER, group);
        var loginMember = new LoginMember(outsider.getId());

        var request =
                new OrganizationMemberRoleUpdateRequest(List.of(userOrgMember.getId()), OrganizationMemberRole.ADMIN);

        assertThatThrownBy(() -> sut.updateRoles(org.getId(), loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 구성원_역할_변경시_대상_구성원_중_일부라도_존재하지_않으면_예외가_발생한다() {
        var org = createOrganization("아맞다");
        var admin = createMember("admin", "admin@email.com");
        var user = createMember("user", "user@email.com");

        var group = createGroup("백엔드");
        var adminOrgMember = createOrganizationMember("admin", admin, org, OrganizationMemberRole.ADMIN, group);
        var userOrgMember = createOrganizationMember("user", user, org, OrganizationMemberRole.USER, group);

        var invalidId = -999L;
        var request = new OrganizationMemberRoleUpdateRequest(
                List.of(userOrgMember.getId(), invalidId),
                OrganizationMemberRole.ADMIN
        );
        var loginMember = new LoginMember(admin.getId());

        assertThatThrownBy(() -> sut.updateRoles(org.getId(), loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 선택된 구성원입니다.");
    }

    @Test
    void 구성원_역할_변경시_대상_구성원이_다른_이벤트_스페이스에_속해있으면_예외가_발생한다() {
        var org1 = createOrganization("아맞다1");
        var org2 = createOrganization("아맞다2");
        var admin = createMember("admin", "admin@email.com");
        var user1 = createMember("user1", "user1@email.com");
        var user2 = createMember("user2", "user2@email.com");

        var group = createGroup("백엔드");
        createOrganizationMember("admin", admin, org1, OrganizationMemberRole.ADMIN, group);
        var user1OrgMember = createOrganizationMember("user1", user1, org1, OrganizationMemberRole.USER, group);
        var user2OrgMember = createOrganizationMember("user2", user2, org2, OrganizationMemberRole.USER, group);

        var request = new OrganizationMemberRoleUpdateRequest(
                List.of(user1OrgMember.getId(), user2OrgMember.getId()),
                OrganizationMemberRole.ADMIN
        );
        var loginMember = new LoginMember(admin.getId());

        assertThatThrownBy(() -> sut.updateRoles(org1.getId(), loginMember, request))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("서로 다른 이벤트 스페이스에 속한 구성원이 포함되어 있습니다.");
    }

    @Test
    void 이벤트_스페이스_구성원_목록을_조회할_수_있다() {
        // given
        var org = createOrganization("우테코");
        var member1 = createMember("홍길동", "hong@email.com");
        var member2 = createMember("박찬호", "chanho@email.com");

        var group = createGroup("백엔드");
        createOrganizationMember("길동", member1, org, OrganizationMemberRole.USER, group);
        createOrganizationMember("찬호", member2, org, OrganizationMemberRole.USER, group);

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
    void 이벤트_스페이스_구성원_목록_조회시_이벤트_스페이스가_존재하지_않으면_예외가_발생한다() {
        // given
        var member = createMember("홍길동", "hong@email.com");
        var loginMember = new LoginMember(member.getId());

        var invalidOrgId = -999L;

        // when // then
        assertThatThrownBy(() -> sut.getAllOrganizationMembers(invalidOrgId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스입니다.");
    }

    @Test
    void 이벤트_스페이스_구성원_목록_조회시_이벤트_스페이스에_속하지_않는_구성원이면_예외가_발생한다() {
        // given
        var org1 = createOrganization("우테코");
        var org2 = createOrganization("다른 이벤트 스페이스");

        var member = createMember("홍길동", "hong@email.com");
        var group = createGroup("백엔드");
        createOrganizationMember("길동", member, org2, OrganizationMemberRole.USER, group); // 다른 이벤트 스페이스 소속

        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getAllOrganizationMembers(org1.getId(), loginMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스에 속한 구성원만 구성원의 목록을 조회할 수 있습니다.");
    }

    @Test
    void 자신의_닉네임을_변경할_수_있다() {
        // given
        var org = createOrganization("우테코");
        var member = createMember("홍길동", "hong@email.com");
        var group = createGroup("백엔드");
        var orgMember = createOrganizationMember("닉네임", member, org, OrganizationMemberRole.USER, group);
        var loginMember = new LoginMember(member.getId());

        var newName = "닉네임2";

        // when
        sut.renameOrganizationMemberNickname(org.getId(), loginMember, newName);

        // then
        assertThat(orgMember.getNickname()).isEqualTo(newName);
    }

    @Test
    void 이미_사용_중인_닉네임으로_닉네임을_변경하면_예외가_발생한다() {
        // given
        var org = createOrganization("우테코");
        var member1 = createMember("홍길동1", "hong1@email.com");
        var group = createGroup("백엔드");
        var orgMember1 = createOrganizationMember("닉네임1", member1, org, OrganizationMemberRole.USER, group);

        var member2 = createMember("홍길동2", "hong@email2.com");
        var orgMember2 = createOrganizationMember("닉네임2", member2, org, OrganizationMemberRole.USER, group);
        var loginMember2 = new LoginMember(member2.getId());

        var duplicateName = "닉네임1";

        // when // then
        assertThatThrownBy(() -> sut.renameOrganizationMemberNickname(org.getId(), loginMember2, duplicateName))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @Test
    void 자신의_닉네임으로_리네임_하면_예외가_발생한다() {
        // given
        var org = createOrganization("우테코");
        var member = createMember("홍길동", "hong1@email.com");
        var group = createGroup("백엔드");
        var orgMember = createOrganizationMember("닉네임1", member, org, OrganizationMemberRole.USER, group);
        var loginMember = new LoginMember(member.getId());

        var myNickname = "닉네임1";

        // when // then
        assertThatThrownBy(() -> sut.renameOrganizationMemberNickname(org.getId(), loginMember, myNickname))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("현재 닉네임과 동일하여 변경할 수 없습니다.");
    }

    @Test
    void 자신의_닉네임과_그룹을_업데이트할_수_있다() {
        // given
        var org = createOrganization("우테코");
        var member = createMember("홍길동", "hong@email.com");
        var group1 = createGroup("백엔드");
        var orgMember = createOrganizationMember("닉네임", member, org, OrganizationMemberRole.USER, group1);
        var loginMember = new LoginMember(member.getId());

        var newGroup = createGroup("프론트"); // 새로운 그룹
        var request = new OrganizationMemberUpdateRequest("새닉네임", newGroup.getId());

        // when
        sut.updateOrganizationMember(org.getId(), loginMember, request);

        // then
        var updated = organizationMemberRepository.findById(orgMember.getId())
                .orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(updated.getNickname())
                    .isEqualTo("새닉네임");
            softly.assertThat(updated.getGroup()
                            .getId())
                    .isEqualTo(newGroup.getId());
        });
    }

    @Test
    void 이미_사용중인_닉네임으로_업데이트하면_예외가_발생한다() {
        // given
        var org = createOrganization("우테코");
        var member1 = createMember("홍길동1", "hong1@email.com");
        var member2 = createMember("홍길동2", "hong2@email.com");

        var group = createGroup("백엔드");
        createOrganizationMember("닉네임1", member1, org, OrganizationMemberRole.USER, group);
        var orgMember2 = createOrganizationMember("닉네임2", member2, org, OrganizationMemberRole.USER, group);

        var loginMember = new LoginMember(member2.getId());
        var request = new OrganizationMemberUpdateRequest("닉네임1", group.getId());

        // when // then
        assertThatThrownBy(() -> sut.updateOrganizationMember(org.getId(), loginMember, request))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @Test
    void 존재하지_않는_그룹으로_업데이트하면_예외가_발생한다() {
        // given
        var org = createOrganization("우테코");
        var member = createMember("홍길동", "hong@email.com");
        var group = createGroup("백엔드");
        var orgMember = createOrganizationMember("닉네임", member, org, OrganizationMemberRole.USER, group);
        var loginMember = new LoginMember(member.getId());

        var invalidGroupId = -999L;
        var request = new OrganizationMemberUpdateRequest("새닉네임", invalidGroupId);

        // when // then
        assertThatThrownBy(() -> sut.updateOrganizationMember(org.getId(), loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 그룹입니다.");
    }

    void 조직_가입_여부를_확인한다() {
        // given
        var enrolledOrg = createOrganization("우테코_가입됨");
        var notEnrolledOrg = createOrganization("우테코_가입안됨");
        var member = createMember("홍길동", "hong1@email.com");
        var group = createGroup();
        var orgMember = createOrganizationMember("닉네임1", member, enrolledOrg, OrganizationMemberRole.USER, group);
        var loginMember = new LoginMember(member.getId());

        // when & then
        assertSoftly(softly -> {
            softly.assertThat(sut.isOrganizationMember(enrolledOrg.getId(), loginMember))
                    .isTrue();
            softly.assertThat(sut.isOrganizationMember(notEnrolledOrg.getId(), loginMember))
                    .isFalse();
        });
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
            OrganizationMemberRole role,
            OrganizationGroup group
    ) {
        return organizationMemberRepository.save(
                OrganizationMember.create(nickname, member, org, role, group)
        );
    }

    private OrganizationGroup createGroup(String name) {
        return organizationGroupRepository.save(OrganizationGroup.create(name));
    }
}
