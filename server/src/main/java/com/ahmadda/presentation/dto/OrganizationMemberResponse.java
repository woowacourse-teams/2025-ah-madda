package com.ahmadda.presentation.dto;

import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.Role;

public record OrganizationMemberResponse(
        Long organizationMemberId,
        String nickname,
        boolean isAdmin
) {

    public static OrganizationMemberResponse from(final OrganizationMember organizationMember) {
        return new OrganizationMemberResponse(
                organizationMember.getId(),
                organizationMember.getNickname(),
                organizationMember.getRole() == Role.ADMIN
        );
    }
}
