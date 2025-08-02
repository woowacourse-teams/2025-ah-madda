package com.ahmadda.domain;

import java.util.List;

public interface PushNotifier {

    void sendPushs(final List<String> recipientPushTokens, final PushNotificationPayload payload);
}
