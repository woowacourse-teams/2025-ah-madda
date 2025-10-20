package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OpenProfileUpdateRequest;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.member.OpenProfile;
import com.ahmadda.domain.member.OpenProfileRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class OpenProfileServiceTest {

    @Autowired
    private OpenProfileService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OpenProfileRepository openProfileRepository;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Test
    void 회원의_오픈_프로필을_조회한다() {
        var member = createMember("홍길동", "hong@email.com");
        var group = createGroup("백엔드");
        var openProfile = createOpenProfile(member, group);
        var loginMember = new LoginMember(member.getId());

        var result = sut.getOpenProfileByMember(loginMember);

        assertSoftly(softly -> {
            softly.assertThat(result.getId())
                    .isEqualTo(openProfile.getId());
            softly.assertThat(result.getMember().getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getOrganizationGroup().getId())
                    .isEqualTo(group.getId());
        });
    }

    @Test
    void 존재하지_않는_회원의_오픈_프로필_조회시_예외가_발생한다() {
        var invalidLoginMember = new LoginMember(999L);

        assertThatThrownBy(() -> sut.getOpenProfileByMember(invalidLoginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 오픈_프로필이_없는_회원_조회시_예외가_발생한다() {
        var member = createMember("홍길동", "hong@email.com");
        var loginMember = new LoginMember(member.getId());

        assertThatThrownBy(() -> sut.getOpenProfileByMember(loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 오픈 프로필입니다.");
    }

    @Test
    void 오픈_프로필을_업데이트한다() {
        var member = createMember("홍길동", "hong@email.com");
        var oldGroup = createGroup("프론트엔드");
        var newGroup = createGroup("백엔드");
        var openProfile = createOpenProfile(member, oldGroup);

        var request = new OpenProfileUpdateRequest("새닉네임", newGroup.getId());

        sut.updateProfile(openProfile.getId(), request);

        var updated = openProfileRepository.findById(openProfile.getId()).orElseThrow();
        assertThat(updated.getOrganizationGroup().getId()).isEqualTo(newGroup.getId());
    }

    @Test
    void 오픈_프로필_업데이트시_관련_조직_구성원도_함께_업데이트된다() {
        var member = createMember("홍길동", "hong@email.com");
        var oldGroup = createGroup("프론트엔드");
        var newGroup = createGroup("백엔드");
        var openProfile = createOpenProfile(member, oldGroup);

        var org1 = createOrganization("우테코1");
        var org2 = createOrganization("우테코2");
        var orgMember1 = createOrganizationMember("구닉네임1", member, org1, oldGroup);
        var orgMember2 = createOrganizationMember("구닉네임2", member, org2, oldGroup);

        var request = new OpenProfileUpdateRequest("새닉네임", newGroup.getId());

        sut.updateProfile(openProfile.getId(), request);

        var updatedOrgMember1 = organizationMemberRepository.findById(orgMember1.getId()).orElseThrow();
        var updatedOrgMember2 = organizationMemberRepository.findById(orgMember2.getId()).orElseThrow();

        assertSoftly(softly -> {
            softly.assertThat(updatedOrgMember1.getNickname()).isEqualTo("새닉네임");
            softly.assertThat(updatedOrgMember1.getGroup().getId()).isEqualTo(newGroup.getId());
            softly.assertThat(updatedOrgMember2.getNickname()).isEqualTo("새닉네임");
            softly.assertThat(updatedOrgMember2.getGroup().getId()).isEqualTo(newGroup.getId());
        });
    }

    @Test
    void 존재하지_않는_오픈_프로필_업데이트시_예외가_발생한다() {
        var group = createGroup("백엔드");
        var request = new OpenProfileUpdateRequest("닉네임", group.getId());

        assertThatThrownBy(() -> sut.updateProfile(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 프로필입니다.");
    }

    @Test
    void 존재하지_않는_그룹으로_업데이트시_예외가_발생한다() {
        var member = createMember("홍길동", "hong@email.com");
        var group = createGroup("백엔드");
        var openProfile = createOpenProfile(member, group);

        var request = new OpenProfileUpdateRequest("닉네임", 999L);

        assertThatThrownBy(() -> sut.updateProfile(openProfile.getId(), request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 그룹입니다.");
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "pic.png"));
    }

    private OrganizationGroup createGroup(String name) {
        return organizationGroupRepository.save(OrganizationGroup.create(name));
    }

    private OpenProfile createOpenProfile(Member member, OrganizationGroup group) {
        return openProfileRepository.save(OpenProfile.create(member, group));
    }

    private Organization createOrganization(String name) {
        return organizationRepository.save(Organization.create(name, "설명", "img.png"));
    }

    private OrganizationMember createOrganizationMember(String nickname, Member member, Organization org, OrganizationGroup group) {
        return organizationMemberRepository.save(
                OrganizationMember.create(nickname, member, org, OrganizationMemberRole.USER, group)
        );
    }
}