package com.ahmadda.application.dto;

import com.ahmadda.domain.Event;

public record EventRead(
        Long eventId,
        LoginMember loginMember
) {

    public static EventRead from(
            final Event event,
            final LoginMember loginMember
    ) {
        return new EventRead(event.getId(), loginMember);
    }
}
