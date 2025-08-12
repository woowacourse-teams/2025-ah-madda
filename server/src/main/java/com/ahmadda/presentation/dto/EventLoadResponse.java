package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;


public record EventLoadResponse(
        Long eventId,
        String title,
        String description,
        int maxCapacity,
        String place
) {

    public static EventLoadResponse from(final Event event) {
        return new EventLoadResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getMaxCapacity(),
                event.getPlace()
        );
    }
}
