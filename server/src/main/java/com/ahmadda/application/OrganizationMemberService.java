package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationMemberRoleUpdateRequest;
import com.ahmadda.application.dto.OrganizationMemberUpdateRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
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
    private final OrganizationGroupRepository organizationGroupRepository;

    @Transactional(readOnly = true)
    public OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 구성원입니다."));
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

    @Transactional
    public void renameOrganizationMemberNickname(
            final Long organizationId,
            final LoginMember loginMember,
            final String newNickName
    ) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);

        if (organizationMember.getNickname()
                .equals(newNickName)) {
            throw new UnprocessableEntityException("현재 닉네임과 동일하여 변경할 수 없습니다.");
        }

        if (organizationMemberRepository.existsByOrganizationIdAndNickname(organizationId, newNickName)) {
            throw new UnprocessableEntityException("이미 사용 중인 닉네임입니다.");
        }

        organizationMember.rename(newNickName);
    }

    @Transactional
    public void updateOrganizationMember(
            final Long organizationId,
            final LoginMember loginMember,
            final OrganizationMemberUpdateRequest request
    ) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);
        OrganizationGroup organizationGroup = getOrganizationGroup(request.groupId());

        if (!organizationMember.isEqualNickname(request.nickname()) && organizationMemberRepository.existsByOrganizationIdAndNickname(
                organizationId,
                request.nickname()
        )) {
            throw new UnprocessableEntityException("이미 사용 중인 닉네임입니다.");
        }

        organizationMember.update(request.nickname(), organizationGroup);
    }

    @Transactional(readOnly = true)
    public boolean isOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.existsByOrganizationIdAndMemberId(organizationId, loginMember.memberId());
    }

    @Transactional(readOnly = true)
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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트 스페이스입니다."));
    }

    private List<OrganizationMember> getAllTargetOrganizationMembers(final List<Long> targetOrganizationMemberIds) {
        List<OrganizationMember> targets = organizationMemberRepository.findAllById(targetOrganizationMemberIds);

        if (targets.size() != targetOrganizationMemberIds.size()) {
            throw new NotFoundException("존재하지 않는 선택된 구성원입니다.");
        }

        return targets;
    }

    private void validateAllBelongToOrganization(
            final Organization organization,
            final List<OrganizationMember> targets
    ) {
        targets.forEach(target -> {
            if (!organization.isExistOrganizationMember(target)) {
                throw new UnprocessableEntityException("서로 다른 이벤트 스페이스에 속한 구성원이 포함되어 있습니다.");
            }
        });
    }

    private OrganizationMember getOperatorOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 구성원입니다."));
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
            throw new ForbiddenException("이벤트 스페이스에 속한 구성원만 구성원의 목록을 조회할 수 있습니다.");
        }
    }

    private OrganizationGroup getOrganizationGroup(final Long groupId) {
        return organizationGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 그룹입니다."));
    }
}
