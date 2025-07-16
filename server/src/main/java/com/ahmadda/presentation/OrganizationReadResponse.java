package com.ahmadda.presentation;

import com.ahmadda.domain.Organization;

public record OrganizationReadResponse(String name, String description, String imageUrl) {

    public static OrganizationReadResponse from(final Organization organization) {
        return new OrganizationReadResponse(organization.getName(),
                                            organization.getDescription(),
                                            organization.getImageUrl()
        );
    }
}
