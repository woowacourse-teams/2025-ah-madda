package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationMemberRoleUpdateRequest;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberService {

    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    @Transactional
    public void updateRoles(
            final LoginMember operatorLoginMember,
            final OrganizationMemberRoleUpdateRequest request
    ) {
        List<OrganizationMember> targets = getAllTargetOrganizationMembers(request.organizationMemberIds());
        Organization targetOrganization = validateAllBelongToSameOrganization(targets);

        OrganizationMember operator = getOperatorOrganizationMember(targetOrganization.getId(), operatorLoginMember);

        updateRoles(operator, targets, request.role());
    }

    private List<OrganizationMember> getAllTargetOrganizationMembers(final List<Long> ids) {
        List<OrganizationMember> targets = organizationMemberRepository.findAllById(ids);

        if (targets.size() != ids.size()) {
            throw new NotFoundException("일부 조직원이 존재하지 않습니다.");
        }

        return targets;
    }

    private Organization validateAllBelongToSameOrganization(final List<OrganizationMember> targets) {
        Organization baseOrganization = targets.get(0)
                .getOrganization();

        boolean allSame = targets.stream()
                .allMatch(member -> member.getOrganization()
                        .equals(baseOrganization));

        if (!allSame) {
            throw new UnprocessableEntityException("모든 대상은 같은 조직에 속해 있어야 합니다.");
        }

        return baseOrganization;
    }

    private OrganizationMember getOperatorOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    private void updateRoles(
            final OrganizationMember operator,
            final List<OrganizationMember> targets,
            final OrganizationMemberRole newRole
    ) {
        for (OrganizationMember target : targets) {
            target.changeRole(operator, newRole);
        }
    }
}
