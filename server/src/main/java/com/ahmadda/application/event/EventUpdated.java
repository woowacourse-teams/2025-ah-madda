package com.ahmadda.application.event;

import com.ahmadda.domain.Event;

public record EventUpdated(Long eventId) {

    public static EventUpdated from(final Event event) {
        return new EventUpdated(event.getId());
    }
}
