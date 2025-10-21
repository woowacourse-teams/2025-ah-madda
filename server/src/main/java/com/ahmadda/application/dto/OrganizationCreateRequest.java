package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationCreateRequest(
        @NotBlank
        String name,
        @NotBlank
        String description
) {

}
