package com.ahmadda.domain;

import com.ahmadda.domain.util.Assert;

public record PushNotificationPayload(
        String title,
        String body,
        Long organizationId,
        Long eventId
) {

    public PushNotificationPayload {
        Assert.notBlank(title, "푸시 제목은 공백일 수 없습니다.");
        Assert.notBlank(body, "푸시 본문은 공백일 수 없습니다.");
        Assert.notNull(organizationId, "조직 ID는 null일 수 없습니다.");
        Assert.notNull(eventId, "이벤트 ID는 null일 수 없습니다.");
    }

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
