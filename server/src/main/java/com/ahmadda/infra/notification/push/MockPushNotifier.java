package com.ahmadda.infra.notification.push;

import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MockPushNotifier implements PushNotifier {

    @Override
    public void sendPushs(final List<String> recipientPushTokens, final PushNotificationPayload payload) {
        log.info(
                "[Mock Push] To: {} | Title: {} | Body: {} | Event ID: {}",
                recipientPushTokens,
                payload.title(),
                payload.body(),
                payload.eventId()
        );
    }
}
