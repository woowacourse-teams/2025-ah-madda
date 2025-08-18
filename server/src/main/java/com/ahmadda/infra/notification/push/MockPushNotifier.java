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
        pushLogging(recipients, pushNotificationPayload);
    }

    @Override
    public void sendPush(final OrganizationMember recipient, final PushNotificationPayload pushNotificationPayload) {
        pushLogging(recipient, pushNotificationPayload);
    }

    private void pushLogging(
            final Object recipients,
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
}
