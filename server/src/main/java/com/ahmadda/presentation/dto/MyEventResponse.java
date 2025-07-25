package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;

import java.time.LocalDateTime;

public record MyEventResponse(
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
        String organizerName,
        boolean isOwned,
        boolean isParticipated
) {

    public static MyEventResponse from(final Event event, boolean isOwned, boolean isParticipated) {
        return new MyEventResponse(
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
                event.getOrganizerNickname(),
                isOwned,
                isParticipated
        );
    }
}
