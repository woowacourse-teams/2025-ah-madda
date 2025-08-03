package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PushNotificationRecipientRequest(
        @NotBlank
        String fcmPushToken
) {

}
