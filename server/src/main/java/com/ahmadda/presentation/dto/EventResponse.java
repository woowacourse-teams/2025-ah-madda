package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.Event;

import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        String title,
        String description,
        LocalDateTime eventStart,
        LocalDateTime eventEnd,
        int currentGuestCount,
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
                event.getGuests()
                        .size(),
                event.getMaxCapacity(),
                event.getPlace(),
                event.getRegistrationStart(),
                event.getRegistrationEnd(),
                event.getOrganizer()
                        .getNickname()
        );
    }
}
