package com.ahmadda.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record EventCreateRequest(
        @NotBlank
        String title,
        @Nullable
        String description,
        @Nullable
        String place,
        @NotNull
        LocalDateTime registrationEnd,
        @NotNull
        LocalDateTime eventStart,
        @NotNull
        LocalDateTime eventEnd,
        int maxCapacity,
        @Valid
        List<QuestionCreateRequest> questions,
        @NotNull
        List<Long> eventOwnerOrganizationMembers
) {

    public EventCreateRequest(
            final String title,
            final String description,
            final String place,
            final LocalDateTime registrationEnd,
            final LocalDateTime eventStart,
            final LocalDateTime eventEnd,
            final int maxCapacity,
            final List<QuestionCreateRequest> questions
    ) {
        this(
                title,
                description,
                place,
                registrationEnd,
                eventStart,
                eventEnd,
                maxCapacity,
                questions,
                List.of()
        );
    }
}
