package com.ahmadda.application;

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
import com.ahmadda.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class OpenProfileServiceTest extends IntegrationTest {

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
        // given
        var group = createGroup("백엔드");
        var member = createMember("홍길동", "hong@email.com");
        var openProfile = createOpenProfile(member, group);
        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.getOpenProfile(loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getMember()
                            .getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getOrganizationGroup()
                            .getId())
                    .isEqualTo(group.getId());
        });
    }

    @Test
    void 존재하지_않는_회원의_오픈_프로필_조회시_예외가_발생한다() {
        // given
        var invalidLoginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.getOpenProfile(invalidLoginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 오픈_프로필이_없는_회원_조회시_예외가_발생한다() {
        // given
        var member = createMember("홍길동", "hong@email.com");
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getOpenProfile(loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 프로필입니다.");
    }

    @Test
    void 오픈_프로필을_업데이트한다() {
        // given
        var oldGroup = createGroup("프론트엔드");
        var newGroup = createGroup("백엔드");

        var member = createMember("홍길동", "hong@email.com");
        var openProfile = createOpenProfile(member, oldGroup);
        var loginMember = new LoginMember(member.getId());

        var request = new OpenProfileUpdateRequest("새닉네임", newGroup.getId());

        // when
        sut.updateProfile(loginMember, request);

        var updated = openProfileRepository.findByMemberId(member.getId())
                .orElseThrow();

        // then
        assertSoftly(softly -> {
            assertThat(updated.getOrganizationGroup()
                    .getId()).isEqualTo(newGroup.getId());
            assertThat(updated.getNickName())
                    .isEqualTo("새닉네임");
        });
    }

    @Test
    void 오픈_프로필_업데이트시_조직_구성원_정보도_함께_업데이트된다() {
        // given
        var oldGroup = createGroup("프론트엔드");
        var newGroup = createGroup("백엔드");

        var member = createMember("홍길동", "hong@email.com");
        var openProfile = createOpenProfile(member, oldGroup);
        var loginMember = new LoginMember(member.getId());

        var org1 = createOrganization("우테코1");
        var orgMember1 = createOrganizationMember("구닉네임1", member, org1, oldGroup);

        var request = new OpenProfileUpdateRequest("새닉네임", newGroup.getId());

        // when
        sut.updateProfile(loginMember, request);

        var updatedOrgMember1 = organizationMemberRepository.findById(orgMember1.getId())
                .orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(updatedOrgMember1.getNickname())
                    .isEqualTo("새닉네임");
            softly.assertThat(updatedOrgMember1.getGroup()
                            .getId())
                    .isEqualTo(newGroup.getId());
        });
    }

    @Test
    void 존재하지_않는_멤버로_프로필_업데이트시_예외가_발생한다() {
        // given
        var group = createGroup("백엔드");
        var request = new OpenProfileUpdateRequest("닉네임", group.getId());
        var notFountLoginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.updateProfile(notFountLoginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 존재하지_않는_그룹으로_업데이트시_예외가_발생한다() {
        // given
        var extraGroup = createGroup("기타");
        var member = createMember("홍길동", "hong@email.com");
        var openProfile = createOpenProfile(member, extraGroup);
        var loginMember = new LoginMember(member.getId());

        var request = new OpenProfileUpdateRequest("닉네임", 999L);

        // when // then
        assertThatThrownBy(() -> sut.updateProfile(loginMember, request))
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
        return openProfileRepository.save(OpenProfile.create(member, member.getName(), group));
    }

    private Organization createOrganization(String name) {
        return organizationRepository.save(Organization.create(name, "설명", "img.png"));
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            Member member,
            Organization org,
            OrganizationGroup group
    ) {
        return organizationMemberRepository.save(
                OrganizationMember.create(nickname, member, org, OrganizationMemberRole.USER, group)
        );
    }
}
