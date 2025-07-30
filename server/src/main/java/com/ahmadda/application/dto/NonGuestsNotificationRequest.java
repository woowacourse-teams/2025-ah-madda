package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record NonGuestsNotificationRequest(
        @NotBlank
        String content
) {

}
