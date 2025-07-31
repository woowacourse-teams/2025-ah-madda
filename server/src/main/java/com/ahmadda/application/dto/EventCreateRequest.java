package com.ahmadda.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record EventCreateRequest(
        @NotBlank
        String title,
        String description,
        String place,
        @NotNull
        LocalDateTime registrationEnd,
        @NotNull
        LocalDateTime eventStart,
        @NotNull
        LocalDateTime eventEnd,
        @NotBlank
        String organizerNickname,
        int maxCapacity,
        @Valid
        List<QuestionCreateRequest> questions
) {

}
