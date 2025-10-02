package com.ahmadda.domain.notification;

import com.ahmadda.domain.organization.OrganizationMember;

import java.util.List;

public interface PushNotifier {

    void remind(final List<OrganizationMember> recipients, final PushNotificationPayload pushNotificationPayload);

    void poke(final OrganizationMember recipient, final PushNotificationPayload pushNotificationPayload);
}
