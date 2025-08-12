package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record TemplateCreateRequest(
        @NotBlank
        String title,
        @NotBlank
        String description
) {

}
