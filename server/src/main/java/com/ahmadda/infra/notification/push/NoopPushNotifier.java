package com.ahmadda.infra.notification.push;

import com.ahmadda.domain.notification.PushNotificationPayload;
import com.ahmadda.domain.notification.PushNotifier;
import com.ahmadda.domain.organization.OrganizationMember;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NoopPushNotifier implements PushNotifier {

    @Override
    public void remind(
            final List<OrganizationMember> recipients,
            final PushNotificationPayload pushNotificationPayload
    ) {
        pushLogging(recipients, pushNotificationPayload);
    }

    @Override
    public void poke(final OrganizationMember recipient, final PushNotificationPayload pushNotificationPayload) {
        pushLogging(recipient, pushNotificationPayload);
    }

    private void pushLogging(
            final Object recipients,
            final PushNotificationPayload pushNotificationPayload
    ) {
        log.info(
                "[Noop Push] To: {} | Title: {} | Body: {} | Event ID: {}",
                recipients,
                pushNotificationPayload.title(),
                pushNotificationPayload.body(),
                pushNotificationPayload.eventId()
        );
    }
}
