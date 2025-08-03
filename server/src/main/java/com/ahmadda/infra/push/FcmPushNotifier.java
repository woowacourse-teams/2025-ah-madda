package com.ahmadda.infra.push;

import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FcmPushNotifier implements PushNotifier {

    private final FcmPushErrorHandler fcmPushErrorHandler;

    @Async
    @Override
    public void sendPushs(final List<String> recipientPushTokens, final PushNotificationPayload payload) {
        if (recipientPushTokens.isEmpty()) {
            return;
        }

        MulticastMessage message = createMulticastMessage(recipientPushTokens, payload);

        try {
            BatchResponse batchResponse = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);

            fcmPushErrorHandler.handleFailures(batchResponse, recipientPushTokens);
        } catch (FirebaseMessagingException e) {
            log.error("fcmPushError: {}", e.getMessage(), e);
        }
    }
    
    private MulticastMessage createMulticastMessage(
            final List<String> recipientPushTokens,
            final PushNotificationPayload payload
    ) {
        return MulticastMessage.builder()
                .addAllTokens(recipientPushTokens)
                .setNotification(Notification.builder()
                        .setTitle(payload.title())
                        .setBody(payload.body())
                        .build())
                .putData(
                        "eventId",
                        payload.eventId()
                                .toString()
                )
                .build();
    }
}
