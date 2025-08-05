package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;


public record EventTemplateResponse(
        Long eventId,
        String title,
        String description,
        int maxCapacity,
        String place
) {

    public static EventTemplateResponse from(final Event event) {
        return new EventTemplateResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getMaxCapacity(),
                event.getPlace()
        );
    }
}
