package com.ahmadda.presentation.dto;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.event.Event;

import java.time.LocalDateTime;

public record MainEventResponse(
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
        boolean isGuest
) {

    public static MainEventResponse from(final Event event, final LoginMember loginMember) {
        boolean isGuest = event.getGuests()
                .stream()
                .anyMatch(guest -> guest.getOrganizationMember()
                        .getMember()
                        .getId()
                        .equals(loginMember.memberId()));

        return new MainEventResponse(
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
                        .getNickname(),
                isGuest
        );
    }
}
