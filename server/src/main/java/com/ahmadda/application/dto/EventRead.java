package com.ahmadda.application.dto;

import com.ahmadda.domain.event.Event;

public record EventRead(
        Long eventId
) {

    public static EventRead from(
            final Event event
    ) {
        return new EventRead(event.getId());
    }
}
