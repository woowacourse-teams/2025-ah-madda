package com.ahmadda.infra.push;

import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FcmPushNotifier implements PushNotifier {

    @Async
    @Override
    public void sendPushs(final List<String> recipientPushTokens, final PushNotificationPayload payload) {
        if (recipientPushTokens.isEmpty()) {
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(payload.title())
                .setBody(payload.body())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(recipientPushTokens)
                .setNotification(notification)
                .putData(
                        "eventId",
                        payload.eventId()
                                .toString()
                )
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);

            int successCount = response.getSuccessCount();
            int failureCount = response.getFailureCount();

            log.info(
                    "푸시 알림 전송 완료 - 총 {}명, 성공 {}, 실패 {}",
                    recipientPushTokens.size(),
                    successCount,
                    failureCount
            );

            if (failureCount > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i)
                            .isSuccessful()) {
                        failedTokens.add(recipientPushTokens.get(i));
                    }
                }

                log.warn("푸시 알림 전송 실패 토큰 목록: {}", failedTokens);
            }

        } catch (FirebaseMessagingException e) {
            log.error("푸시 알림 전송 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
