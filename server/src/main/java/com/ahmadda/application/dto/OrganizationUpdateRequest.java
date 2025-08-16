package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationUpdateRequest(
        @NotBlank
        String name,
        @NotBlank
        String description
) {

}
