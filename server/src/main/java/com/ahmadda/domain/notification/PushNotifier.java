package com.ahmadda.domain.notification;

import com.ahmadda.domain.organization.OrganizationMember;

import java.util.List;

public interface PushNotifier {

    void sendPushs(final List<OrganizationMember> recipients, final PushNotificationPayload pushNotificationPayload);

    void sendPush(
            final OrganizationMember recipient,
            final PushNotificationPayload pushNotificationPayload
    );
}
