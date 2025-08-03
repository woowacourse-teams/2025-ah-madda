package com.ahmadda.infra.push;

import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class FcmPushNotifier implements PushNotifier {

    // https://firebase.google.com/docs/reference/fcm/rest/v1/ErrorCode
    private static final Set<MessagingErrorCode> DELETABLE_ERRORS = Set.of(
            MessagingErrorCode.UNREGISTERED,
            MessagingErrorCode.INVALID_ARGUMENT
    );
    private static final Set<MessagingErrorCode> RETRYABLE_ERRORS = Set.of(
            MessagingErrorCode.UNAVAILABLE,
            MessagingErrorCode.QUOTA_EXCEEDED,
            MessagingErrorCode.INTERNAL
    );

    @Async
    @Override
    public void sendPushs(final List<String> recipientPushTokens, final PushNotificationPayload payload) {
        if (recipientPushTokens.isEmpty()) {
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
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

        try {
            BatchResponse response = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);
            log.info(
                    "푸시 알림 전송 완료 - 총 {}명, 성공 {}, 실패 {}",
                    recipientPushTokens.size(), response.getSuccessCount(), response.getFailureCount()
            );

            handleFailures(response, recipientPushTokens);

        } catch (FirebaseMessagingException e) {
            log.error("푸시 알림 전송 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    private void handleFailures(final BatchResponse response, final List<String> tokens) {
        final List<SendResponse> responses = response.getResponses();
        final List<String> failedTokens = new ArrayList<>();

        for (int i = 0; i < responses.size(); i++) {
            final SendResponse sendResponse = responses.get(i);
            if (!sendResponse.isSuccessful()) {
                final String token = tokens.get(i);
                failedTokens.add(token);

                final FirebaseMessagingException exception = sendResponse.getException();
                final MessagingErrorCode errorCode = exception.getMessagingErrorCode();

                if (DELETABLE_ERRORS.contains(errorCode)) {
                    // TODO: 저장소에서 토큰 삭제
                    log.info("삭제 대상 토큰 감지 - {}, 이유: {}", token, errorCode);
                }

                if (RETRYABLE_ERRORS.contains(errorCode)) {
                    log.warn("재시도 가능한 에러 발생 - 토큰: {}, 이유: {}", token, errorCode);
                    // TODO: 재시도 큐나 로그 저장소로 보낼 수 있음
                } else {
                    log.warn("푸시 실패 - 토큰: {}, 이유: {}, 메시지: {}", token, errorCode, exception.getMessage());
                }
            }
        }

        if (!failedTokens.isEmpty()) {
            log.warn("푸시 알림 전송 실패 토큰 목록: {}", failedTokens);
        }
    }
}
