package com.ahmadda.domain.notification;

import com.ahmadda.domain.event.Event;

public record PushNotificationPayload(
        String title,
        String body,
        Long organizationId,
        Long eventId
) {

    public static PushNotificationPayload of(final Event event, final String content) {
        return new PushNotificationPayload(
                event.getTitle(),
                content,
                event.getOrganization()
                        .getId(),
                event.getId()
        );
    }
}
