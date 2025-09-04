package com.ahmadda.application.dto;

import com.ahmadda.domain.event.Event;

public record EventUpdated(Long eventId) {

    public static EventUpdated from(final Event event) {
        return new EventUpdated(event.getId());
    }
}
