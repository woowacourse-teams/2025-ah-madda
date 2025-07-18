package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Organization;

public record OrganizationReadResponse(Long id, String name, String description, String imageUrl) {

    public static OrganizationReadResponse from(final Organization organization) {
        return new OrganizationReadResponse(organization.getId(),
                                            organization.getName(),
                                            organization.getDescription(),
                                            organization.getImageUrl()
        );
    }
}
