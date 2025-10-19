package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOrganizer;

import java.time.LocalDateTime;
import java.util.List;

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
        List<String> organizerNicknames,
        boolean isGuest
) {

    public static MainEventResponse from(final Event event) {
        List<String> organizerNicknames = getOrganizerNicknames(event);

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
                organizerNicknames,
                // TODO. 추후 비회원 여부에 따른 isGuest 값 설정 필요
                false
        );
    }

    private static List<String> getOrganizerNicknames(Event event) {
        return event.getEventOrganizers()
                .stream()
                .map(EventOrganizer::getNickname)
                .toList();
    }
}
