package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Organization;

public record OrganizationResponse(
        Long organizationId,
        String name,
        String description,
        String imageUrl
) {

    public static OrganizationResponse from(final Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getDescription(),
                organization.getImageUrl()
        );
    }
}
