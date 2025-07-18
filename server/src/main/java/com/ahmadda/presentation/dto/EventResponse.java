package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;

import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        String title,
        String description,
        LocalDateTime eventStart,
        LocalDateTime eventEnd,
        int maxCapacity,
        String place,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd,
        String organizerName
) {

    public static EventResponse from(final Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventStart(),
                event.getEventEnd(),
                event.getMaxCapacity(),
                event.getPlace(),
                event.getRegistrationStart(),
                event.getRegistrationEnd(),
                event.getOrganizer().getNickname()
        );
    }
}
