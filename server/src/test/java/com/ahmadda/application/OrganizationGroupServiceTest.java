package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.GroupCreateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.GroupRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ahmadda.domain.organization.OrganizationMemberRole.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class OrganizationGroupServiceTest {

    @Autowired
    private OrganizationGroupService sut;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Test
    void 어드민은_그룹을_생성할_수_있다() {
        //given
        var organization = createOrganizationAndSave("우테코");
        var member = createMemberAndSave("이재훈", "dlwogns3413@gmail.com");
        var organizationMember = createOrganizationMemberAndSave("서프", member, organization, ADMIN);
        var groupCreateRequest = new GroupCreateRequest("프론트");

        //when
        var group = sut.createGroup(organization.getId(), groupCreateRequest, new LoginMember(member.getId()));

        //then
        assertThat(groupRepository.findById(group.getId()))
                .hasValueSatisfying(g -> assertThat(g)
                        .extracting("name", "organization")
                        .containsExactly("프론트", organization));
    }

    @Test
    void 이벤트_스페이스에_이미_존재하는_이름의_그룹을_생성할때_예외가_발생한다() {
        //given
        var organization = createOrganizationAndSave("우테코");
        var member = createMemberAndSave("이재훈", "dlwogns3413@gmail.com");
        var organizationMember = createOrganizationMemberAndSave("서프", member, organization, ADMIN);
        var groupCreateRequest = new GroupCreateRequest("프론트");
        groupRepository.save(OrganizationGroup.create("프론트", organization, organizationMember));

        //when //then
        assertThatThrownBy(() -> sut.createGroup(
                organization.getId(),
                groupCreateRequest,
                new LoginMember(member.getId())
        ))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("그룹 이름이 이벤트 스페이스에 이미 존재합니다.");
    }

    @Test
    void 존재하지_않는_이벤트_스페이스에_그룹을_생성하려고하면_예외가_발생한다() {
        //given
        var organization = createOrganizationAndSave("우테코");
        var member = createMemberAndSave("이재훈", "dlwogns3413@gmail.com");
        var organizationMember = createOrganizationMemberAndSave("서프", member, organization, ADMIN);
        var groupCreateRequest = new GroupCreateRequest("프론트");

        //when //then
        assertThatThrownBy(() -> sut.createGroup(999L, groupCreateRequest, new LoginMember(member.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스 정보입니다.");
    }

    @Test
    void 존재하지_않는_구성원이_그룹을_생성하려_한다면_예외가_발생한다() {
        //given
        var organization = createOrganizationAndSave("우테코");
        var member = createMemberAndSave("이재훈", "dlwogns3413@gmail.com");
        var groupCreateRequest = new GroupCreateRequest("프론트");

        //when //then
        assertThatThrownBy(() -> sut.createGroup(
                organization.getId(),
                groupCreateRequest,
                new LoginMember(member.getId())
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    private Member createMemberAndSave(String name, String email) {
        var member = Member.create(name, email, "profileUrl");
        return memberRepository.save(member);
    }

    private Organization createOrganizationAndSave(String name) {
        var organization = Organization.create(name, "description", "imageUrl");
        return organizationRepository.save(organization);
    }

    private OrganizationMember createOrganizationMemberAndSave(
            String nickname,
            Member member,
            Organization organization,
            OrganizationMemberRole role
    ) {
        var organizationMember = OrganizationMember.create(
                nickname,
                member,
                organization,
                role
        );
        return organizationMemberRepository.save(organizationMember);
    }
}
