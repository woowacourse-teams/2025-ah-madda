package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record EventTemplateCreateRequest(
        @NotBlank
        String title,
        @NotBlank
        String description
) {

}
