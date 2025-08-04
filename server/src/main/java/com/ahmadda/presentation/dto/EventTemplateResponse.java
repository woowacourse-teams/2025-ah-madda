package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;

public record EventTemplateResponse(
        Long eventId,
        String tile,
        String description,
        String organizerName
) {

    public static EventTemplateResponse from(final Event event) {
        return new EventTemplateResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getOrganizerNickname()
        );
    }
}
