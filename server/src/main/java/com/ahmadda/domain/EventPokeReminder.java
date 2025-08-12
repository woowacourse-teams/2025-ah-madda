package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//TODO 디비 방식으로 구현하면 db io에 매우 부하가 클것으로 예상됨
// 메모리 방식의 부하 고려가능
// 만약 고려한다면 메모리 부하를 예상하여 분산 필요
// 삭제 스케줄링 필요
@Component
@RequiredArgsConstructor
public class EventPokeReminder {

    private static final int MAX_SENDABLE_COUNT = 5;
    private static final String POKE_MESSAGE_FORMAT = "%s님에게 포키가 왔습니다!";

    private final PushNotifier pushNotifier;
    private final EventPokeHistoryRepository eventPokeHistoryRepository;

    @Transactional
    public void sendPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event
    ) {
        int count = eventPokeHistoryRepository.countEventPokeHistoryByEventAndSenderAndRecipient(
                event,
                sender,
                recipient
        );

        if (count >= MAX_SENDABLE_COUNT) {
            throw new BusinessRuleViolatedException("포키는 한 대상에게 최대 5번만 보낼 수 있습니다.");
        }

        pushPoke(sender, recipient, event);

        EventPokeHistory eventPokeHistory =
                EventPokeHistory.create(sender, recipient, event);

        eventPokeHistoryRepository.save(eventPokeHistory);
    }

    private void pushPoke(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember,
            final Event event
    ) {
        String sendMessage = String.format(POKE_MESSAGE_FORMAT, sendOrganizationMember.getNickname());

        pushNotifier.sendPush(
                receiveOrganizationMember,
                PushNotificationPayload.of(
                        event,
                        sendMessage
                )
        );
    }
}
