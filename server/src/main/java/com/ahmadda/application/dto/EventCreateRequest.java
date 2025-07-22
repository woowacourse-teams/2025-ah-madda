package com.ahmadda.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record EventCreateRequest(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String place,
        @NotNull
        LocalDateTime registrationStart,
        @NotNull
        LocalDateTime registrationEnd,
        @NotNull
        LocalDateTime eventStart,
        @NotNull
        LocalDateTime eventEnd,
        int maxCapacity,
        @Valid
        List<QuestionCreateRequest> questions
) {

}
