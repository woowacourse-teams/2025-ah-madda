package com.ahmadda.presentation.dto;

import com.ahmadda.domain.organization.OrganizationMember;

public record OrganizationMemberResponse(
        Long organizationMemberId,
        String nickname,
        boolean isAdmin,
        OrganizationGroupResponse group
) {

    public static OrganizationMemberResponse from(final OrganizationMember organizationMember) {
        return new OrganizationMemberResponse(
                organizationMember.getId(),
                organizationMember.getNickname(),
                organizationMember.isAdmin(),
                OrganizationGroupResponse.from(organizationMember.getGroup())
        );
    }
}
