package com.ahmadda.domain;

import java.util.List;

public interface PushNotifier {

    void sendPushs(final List<OrganizationMember> recipients, final PushNotificationPayload pushNotificationPayload);

    void sendPush(
            final OrganizationMember recipient,
            final PushNotificationPayload pushNotificationPayload
    );
}
