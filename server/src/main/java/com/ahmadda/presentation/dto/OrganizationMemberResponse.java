package com.ahmadda.presentation.dto;

import com.ahmadda.domain.OrganizationMember;

public record OrganizationMemberResponse(
        Long id,
        String nickname
) {

    public static OrganizationMemberResponse from(final OrganizationMember organizationMember) {
        return new OrganizationMemberResponse(
                organizationMember.getId(),
                organizationMember.getNickname()
        );
    }
}
