package com.ahmadda.presentation.dto;

import com.ahmadda.domain.notification.PokeMessage;
import jakarta.validation.constraints.NotNull;

public record PokeRequest(
        @NotNull
        Long receiptOrganizationMemberId,
        @NotNull
        PokeMessage pokeMessage
) {

}
