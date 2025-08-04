package com.ahmadda.presentation.dto;

import java.time.LocalDateTime;

public record InviteCodeCreateResponse(
        String inviteCode,
        LocalDateTime expiresAt
) {

}
