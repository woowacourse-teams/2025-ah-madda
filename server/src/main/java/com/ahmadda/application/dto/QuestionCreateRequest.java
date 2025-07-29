package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionCreateRequest(
        @NotBlank
        String questionText,
        boolean isRequired
) {

}
