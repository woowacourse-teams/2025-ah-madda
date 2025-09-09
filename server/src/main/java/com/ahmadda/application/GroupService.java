package com.ahmadda.application;

import com.ahmadda.application.dto.GroupCreateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.organization.Group;
import com.ahmadda.domain.organization.GroupRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Group createGroup(
            final Long organizationId,
            final GroupCreateRequest groupCreateRequest,
            final LoginMember loginMember
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember.memberId());

        Group group = Group.create(groupCreateRequest.name(), organization, organizationMember);

        return groupRepository.save(group);
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트 스페이스 정보입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final Long memberId) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 구성원입니다."));
    }
}
