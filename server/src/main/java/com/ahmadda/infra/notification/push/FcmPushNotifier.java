package com.ahmadda.infra.notification.push;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FcmPushNotifier implements PushNotifier {

    private final FcmRegistrationTokenRepository fcmRegistrationTokenRepository;
    private final FcmPushErrorHandler fcmPushErrorHandler;
    private final NotificationProperties notificationProperties;

    @Async
    @Override
    public void sendPushs(
            final List<OrganizationMember> recipients,
            final PushNotificationPayload pushNotificationPayload
    ) {
        if (recipients.isEmpty()) {
            return;
        }
        List<String> registrationTokens = getRegistrationTokens(recipients);
        if (registrationTokens.isEmpty()) {
            return;
        }

        MulticastMessage message = createMulticastMessage(registrationTokens, pushNotificationPayload);
        try {
            // TODO. 추후 한번에 500개 이상의 토큰을 처리한다면 배치 처리를 고려해야 함
            BatchResponse batchResponse = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);

            fcmPushErrorHandler.handleFailures(batchResponse, registrationTokens);
        } catch (FirebaseMessagingException e) {
            log.error("fcmMulticastPushError: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendPush(OrganizationMember recipient, PushNotificationPayload pushNotificationPayload) {
        String registrationToken = getRegistrationToken(recipient);
        Message message = createSingleMessage(registrationToken, pushNotificationPayload);

        try {
            FirebaseMessaging.getInstance()
                    .send(message);

        } catch (FirebaseMessagingException e) {
            fcmPushErrorHandler.handleFailure(registrationToken, e);
        }
    }

    private List<String> getRegistrationTokens(final List<OrganizationMember> recipients) {
        List<Long> memberIds = recipients.stream()
                .map(organizationMember -> organizationMember.getMember()
                        .getId())
                .toList();

        return fcmRegistrationTokenRepository.findAllByMemberIdIn(memberIds)
                .stream()
                .map(FcmRegistrationToken::getRegistrationToken)
                .toList();
    }

    private String getRegistrationToken(final OrganizationMember recipient) {
        Long memberId = recipient.getMember()
                .getId();

        try {
            return fcmRegistrationTokenRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new NotFoundException("유저의 fcm 토큰을 찾는데 실패했습니다."))
                    .getRegistrationToken();
        } catch (BusinessFlowViolatedException exception) {
            log.error(
                    "유저의 fcm 토큰을 찾는데 실패했습니다. 대상 멤버 id: {}",
                    recipient.getMember()
                            .getId(),
                    exception
            );
            throw exception;
        }
    }

    private Message createSingleMessage(
            final String recipientPushToken,
            final PushNotificationPayload payload
    ) {
        return Message.builder()
                .setToken(recipientPushToken)
                .setNotification(Notification.builder()
                        .setTitle(payload.title())
                        .setBody(payload.body())
                        .build())
                .putData("redirectUrl", notificationProperties.getRedirectUrlPrefix() + payload.eventId())
                .build();
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
                .putData("redirectUrl", notificationProperties.getRedirectUrlPrefix() + payload.eventId())
                .build();
    }
}
