package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ParticipateRequestDto(
        @NotBlank String nickname
) {

}
