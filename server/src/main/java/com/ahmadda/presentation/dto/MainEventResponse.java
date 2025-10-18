package com.ahmadda.presentation.dto;

import com.ahmadda.application.dto.LoginMember;
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
        boolean isOrganizer,
        boolean isGuest
) {

    public static MainEventResponse from(final Event event, final LoginMember loginMember) {
        List<String> organizerNicknames = getOrganizerNicknames(event);
        boolean isOrganizer = isOrganizerOf(event, loginMember);
        boolean isGuest = isGuestOf(event, loginMember);

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
                isOrganizer,
                isGuest
        );
    }

    private static List<String> getOrganizerNicknames(final Event event) {
        return event.getEventOrganizers()
                .stream()
                .map(EventOrganizer::getNickname)
                .toList();
    }

    private static boolean isOrganizerOf(final Event event, final LoginMember loginMember) {
        if (loginMember == null) {
            return false;
        }

        return event.getEventOrganizers()
                .stream()
                .anyMatch(organizer ->
                        organizer.getOrganizationMember()
                                .getMember()
                                .getId()
                                .equals(loginMember.memberId()));
    }

    private static boolean isGuestOf(final Event event, final LoginMember loginMember) {
        if (loginMember == null) {
            return false;
        }

        return event.getGuests()
                .stream()
                .anyMatch(guest -> guest.getOrganizationMember()
                        .getMember()
                        .getId()
                        .equals(loginMember.memberId()));
    }
}
