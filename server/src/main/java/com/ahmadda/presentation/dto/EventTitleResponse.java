package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;

public record EventTitleResponse(
        Long eventId,
        String title) {

    public static EventTitleResponse from(final Event event) {
        return new EventTitleResponse(
                event.getId(),
                event.getTitle()
        );
    }
}
