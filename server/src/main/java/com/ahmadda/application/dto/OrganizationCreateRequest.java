package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationCreateRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        String imageUrl
) {

}
