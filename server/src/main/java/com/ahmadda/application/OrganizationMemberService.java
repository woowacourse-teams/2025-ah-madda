package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationMemberRoleUpdateRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberService {

    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;

    public OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    @Transactional
    public void updateRoles(
            final Long organizationId,
            final LoginMember operatorLoginMember,
            final OrganizationMemberRoleUpdateRequest request
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember operator = getOperatorOrganizationMember(organizationId, operatorLoginMember);

        List<OrganizationMember> targets = getAllTargetOrganizationMembers(request.organizationMemberIds());
        validateAllBelongToOrganization(organization, targets);

        updateRoles(operator, targets, request.role());
    }

    public List<OrganizationMember> getAllOrganizationMembers(
            final Long organizationId,
            final LoginMember loginMember
    ) {
        Organization organization = getOrganization(organizationId);

        validateBelongsToOrganization(organizationId, loginMember);

        return organization.getOrganizationMembers();
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직입니다."));
    }

    private List<OrganizationMember> getAllTargetOrganizationMembers(final List<Long> ids) {
        List<OrganizationMember> targets = organizationMemberRepository.findAllById(ids);

        if (targets.size() != ids.size()) {
            throw new NotFoundException("일부 조직원이 존재하지 않습니다.");
        }

        return targets;
    }

    private void validateAllBelongToOrganization(
            final Organization organization,
            final List<OrganizationMember> targets
    ) {
        targets.forEach(target -> {
            if (!organization.isExistOrganizationMember(target)) {
                throw new UnprocessableEntityException("서로 다른 조직에 속한 조직원이 포함되어 있습니다.");
            }
        });
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
        operator.changeRolesOf(targets, newRole);
    }

    private void validateBelongsToOrganization(final Long organizationId, final LoginMember loginMember) {
        if (!organizationMemberRepository.existsByOrganizationIdAndMemberId(organizationId, loginMember.memberId())) {
            throw new ForbiddenException("조직에 속한 조직원만 조직원의 목록을 조회할 수 있습니다.");
        }
    }
}
