package com.ahmadda.infra.notification.push;

import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class FcmPushNotifier implements PushNotifier {

    private final FcmPushErrorHandler fcmPushErrorHandler;
    private final NotificationProperties notificationProperties;

    @Async
    @Override
    public void sendPushs(final List<String> recipientPushTokens, final PushNotificationPayload payload) {
        if (recipientPushTokens.isEmpty()) {
            return;
        }

        MulticastMessage message = createMulticastMessage(recipientPushTokens, payload);

        try {
            // TODO. 추후 한번에 500개 이상의 토큰을 처리한다면 배치 처리를 고려해야 함
            BatchResponse batchResponse = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);

            fcmPushErrorHandler.handleFailures(batchResponse, recipientPushTokens);
        } catch (FirebaseMessagingException e) {
            log.error("fcmMulticastPushError: {}", e.getMessage(), e);
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
                .putAllData(Map.of(
                        "eventId", payload.eventId()
                                .toString(),
                        "redirectUrl",
                        notificationProperties.getRedirectUrlPrefix() + payload.eventId()
                ))
                .build();
    }
}
