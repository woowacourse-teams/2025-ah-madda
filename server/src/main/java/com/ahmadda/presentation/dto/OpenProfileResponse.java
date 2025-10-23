package com.ahmadda.presentation.dto;

import com.ahmadda.domain.member.OpenProfile;

public record OpenProfileResponse(
        Long id,
        String name,
        String email,
        String picture,
        Long groupId,
        String groupName
) {

    public static OpenProfileResponse from(final OpenProfile openProfile) {
        return new OpenProfileResponse(
                openProfile.getId(),
                openProfile.getNickname(),
                openProfile.getEmail(),
                openProfile.getProfileImageUrl(),
                openProfile.getOrganizationGroup()
                        .getId(),
                openProfile.getOrganizationGroup()
                        .getName()
        );
    }
}
