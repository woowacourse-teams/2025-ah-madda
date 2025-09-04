package com.ahmadda.presentation.dto;

import com.ahmadda.domain.organization.OrganizationMember;

public record OrganizationMemberResponse(
        Long organizationMemberId,
        String nickname,
        boolean isAdmin
) {

    public static OrganizationMemberResponse from(final OrganizationMember organizationMember) {
        return new OrganizationMemberResponse(
                organizationMember.getId(),
                organizationMember.getNickname(),
                organizationMember.isAdmin()
        );
    }
}
