package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OpenProfileUpdateRequest;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.member.OpenProfile;
import com.ahmadda.domain.member.OpenProfileRepository;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenProfileService {

    private final OpenProfileRepository openProfileRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationGroupRepository organizationGroupRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public OpenProfile getOpenProfile(final LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        return openProfileRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 프로필입니다."));
    }

    @Transactional
    public void updateProfile(final LoginMember loginMember, final OpenProfileUpdateRequest request) {
        OpenProfile openProfile = getOpenProfile(loginMember);
        OrganizationGroup organizationGroup = getOrganizationGroup(request.groupId());

        openProfile.updateProfile(organizationGroup);

        List<OrganizationMember> organizationMembers =
                organizationMemberRepository.findAllByMember(openProfile.getMember());

        for (OrganizationMember organizationMember : organizationMembers) {
            organizationMember.update(request.nickname(), organizationGroup);
        }
    }

    private OrganizationGroup getOrganizationGroup(final Long groupId) {
        return organizationGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 그룹입니다."));
    }
}
