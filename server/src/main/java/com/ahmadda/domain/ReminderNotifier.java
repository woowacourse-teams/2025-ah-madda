package com.ahmadda.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderNotifier {

    private final EmailNotifier emailNotifier;
    private final PushNotifier pushNotifier;

    // TODO. 추후 recipients의 알람 타입 에 따라 이메일, 푸시 알림을 선택적으로 보낼 수 있도록 구현
    public void remind(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        EventEmailPayload emailPayload = EventEmailPayload.of(event, content);
        emailNotifier.sendEmails(recipients, emailPayload);

        PushNotificationPayload pushPayload = PushNotificationPayload.of(event, content);
        pushNotifier.sendPushs(recipients, pushPayload);
    }
}
