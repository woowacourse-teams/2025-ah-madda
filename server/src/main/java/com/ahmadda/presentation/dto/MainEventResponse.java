package com.ahmadda.presentation.dto;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOwnerOrganizationMember;

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

    public static MainEventResponse from(final Event event, final LoginMember loginMember) {
        boolean isGuest = getIsGuest(event, loginMember);

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
                isGuest
        );
    }

    private static List<String> getOrganizerNicknames(Event event) {
        return event.getEventOwnerOrganizationMembers()
                .stream()
                .map(EventOwnerOrganizationMember::getNickname)
                .toList();
    }

    private static boolean getIsGuest(Event event, LoginMember loginMember) {
        return event.getGuests()
                .stream()
                .anyMatch(guest -> guest.getOrganizationMember()
                        .getMember()
                        .getId()
                        .equals(loginMember.memberId()));
    }
}
