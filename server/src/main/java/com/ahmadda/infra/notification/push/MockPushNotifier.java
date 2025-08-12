package com.ahmadda.infra.notification.push;

import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MockPushNotifier implements PushNotifier {

    @Override
    public void sendPushs(
            final List<OrganizationMember> recipients,
            final PushNotificationPayload pushNotificationPayload
    ) {
        log.info(
                "[Mock Push] To: {} | Title: {} | Body: {} | Event ID: {}",
                recipients,
                pushNotificationPayload.title(),
                pushNotificationPayload.body(),
                pushNotificationPayload.eventId()
        );
    }

    @Override
    public void sendPush(OrganizationMember recipient, PushNotificationPayload pushNotificationPayload) {
        log.info(
                "[Mock Push] To: {} | Title: {} | Body: {} | Event ID: {}",
                recipient,
                pushNotificationPayload.title(),
                pushNotificationPayload.body(),
                pushNotificationPayload.eventId()
        );
    }
}
