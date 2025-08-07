package com.ahmadda.application.dto;

public record EventCreated(
        Long eventId
) {

    public static EventCreated from(final Long id) {
        return new EventCreated(id);
    }
}
