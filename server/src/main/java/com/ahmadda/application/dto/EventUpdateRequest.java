package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

public record EventUpdateRequest(
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
        @NotBlank
        String organizerNickname,
        int maxCapacity
) {

}
