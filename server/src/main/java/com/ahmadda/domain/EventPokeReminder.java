package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

//TODO 디비 방식으로 구현하면 db io에 매우 부하가 클것으로 예상됨
// 메모리 방식의 부하 고려가능
// 만약 고려한다면 메모리 부하를 예상하여 분산 필요
// 삭제 스케줄링 필요
@Component
@RequiredArgsConstructor
public class EventPokeReminder {

    private static final int MAX_SENDABLE_COUNT = 10;
    private static final Duration DUPLICATE_POKE_COUNT_MINUTES = Duration.ofMinutes(30L);
    private static final String POKE_MESSAGE_FORMAT = "%s님에게 포키가 왔습니다!";

    private final PushNotifier pushNotifier;
    private final EventPokeRepository eventPokeRepository;

    @Transactional
    public void sendPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime dateTime
    ) {
        LocalDateTime findDuplicateStartTime = dateTime.minus(DUPLICATE_POKE_COUNT_MINUTES);

        int count = eventPokeRepository.countEventPokeHistoryByEventAndSenderAndRecipientAndSentAtAfter(
                event,
                sender,
                recipient,
                findDuplicateStartTime
        );

        if (count >= MAX_SENDABLE_COUNT) {
            throw new BusinessRuleViolatedException("포키는 30분마다 한 대상에게 최대 10번만 보낼 수 있습니다.");
        }

        EventPoke eventPoke = EventPoke.create(sender, recipient, event, dateTime);
        pushPoke(sender, recipient, event);


        eventPokeRepository.save(eventPoke);
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
