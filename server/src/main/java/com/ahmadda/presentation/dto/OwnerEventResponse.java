package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOrganizer;

import java.time.LocalDateTime;
import java.util.List;

public record OwnerEventResponse(
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
        List<String> organizerNicknames,
        OrganizationResponse organization
) {

    public static OwnerEventResponse from(final Event event) {
        return new OwnerEventResponse(
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
                event.getEventOrganizers()
                        .stream()
                        .map(EventOrganizer::getNickname)
                        .toList(),
                OrganizationResponse.from(event.getOrganization())
        );
    }
}
