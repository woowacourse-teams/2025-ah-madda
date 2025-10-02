package com.ahmadda.infra.notification.push;

import com.ahmadda.domain.notification.PushNotificationPayload;
import com.ahmadda.domain.notification.PushNotifier;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import jakarta.persistence.EntityManager;
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
    private final EntityManager em;

    @Async
    @Override
    public void remind(
            final List<OrganizationMember> recipients,
            final PushNotificationPayload pushNotificationPayload
    ) {
        if (recipients.isEmpty()) {
            return;
        }

        List<String> registrationTokens = getRegistrationTokens(recipients);
        sendMulticast(pushNotificationPayload, registrationTokens);
    }

    @Async
    @Override
    public void poke(final OrganizationMember recipient, final PushNotificationPayload pushNotificationPayload) {
        List<String> registrationTokens = getRegistrationTokens(recipient);
        sendMulticast(pushNotificationPayload, registrationTokens);
    }

    private List<String> getRegistrationTokens(final List<OrganizationMember> recipients) {
        List<Long> memberIds = recipients.stream()
                .map(organizationMember -> organizationMember.getMember()
                        .getId())
                .toList();

        return fcmRegistrationTokenRepository.findAllByMemberIdIn(memberIds)
                .stream()
                .map(FcmRegistrationToken::getRegistrationToken)
                .distinct()
                .toList();
    }

    private List<String> getRegistrationTokens(final OrganizationMember recipient) {
        long memberId = recipient.getMember()
                .getId();

        return fcmRegistrationTokenRepository.findAllByMemberId(memberId)
                .stream()
                .map(FcmRegistrationToken::getRegistrationToken)
                .distinct()
                .toList();
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
                        "redirectUrl",
                        notificationProperties.getRedirectUrlPrefix() + payload.organizationId() + "/event/" + payload.eventId()
                )
                .build();
    }

    private void sendMulticast(
            final PushNotificationPayload pushNotificationPayload,
            final List<String> registrationTokens
    ) {
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
}
