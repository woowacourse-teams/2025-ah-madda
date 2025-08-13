package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

//TODO 성능 문제 추후에 고려
@Service
@RequiredArgsConstructor
public class Poke {

    private static final int MAX_SENDABLE_COUNT = 10;
    private static final Duration DUPLICATE_POKE_COUNT_MINUTES = Duration.ofMinutes(30L);
    private static final String POKE_MESSAGE_FORMAT = "%s님에게 포키가 왔습니다!";

    private final PushNotifier pushNotifier;
    private final PokeHistoryRepository pokeHistoryRepository;

    @Transactional
    public void doPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime sentAt
    ) {
        validateDoPoke(sender, recipient, event, sentAt);
        validateDuplicateDoPoke(sender, recipient, event, sentAt);

        PokeHistory pokeHistory = PokeHistory.create(sender, recipient, event, sentAt);
        pushPoke(sender, recipient, event);

        pokeHistoryRepository.save(pokeHistory);
    }

    private void validateDuplicateDoPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime sentAt
    ) {
        LocalDateTime findDuplicateStartTime = sentAt.minus(DUPLICATE_POKE_COUNT_MINUTES);

        int count = pokeHistoryRepository.countPokeHistoryByEventAndSenderAndRecipientAndSentAtAfter(
                event,
                sender,
                recipient,
                findDuplicateStartTime
        );

        if (count >= MAX_SENDABLE_COUNT) {
            throw new BusinessRuleViolatedException("포키는 30분마다 한 대상에게 최대 10번만 보낼 수 있습니다.");
        }
    }

    private void validateDoPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime sentAt
    ) {
        validateEvent(event);
        validatePokeOrganizationMembers(sender, recipient);
        validateOrganizationParticipate(event, sender, recipient);
        validateReceiveOrganizationMember(event, recipient);
        validateSentAt(sentAt);
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

    private void validateSentAt(final LocalDateTime sentAt) {
        Assert.notNull(sentAt, "포키 전송 날짜는 null 일 수 없습니다.");
    }

    private void validateReceiveOrganizationMember(
            final Event event,
            final OrganizationMember receiveOrganizationMember
    ) {

        if (event.hasGuest(receiveOrganizationMember)) {
            throw new BusinessRuleViolatedException("이미 이벤트에 참여한 조직원에게 포키를 보낼 수 없습니다.");
        }

        if (event.isOrganizer(receiveOrganizationMember)) {
            throw new BusinessRuleViolatedException("주최자에게 포키를 보낼 수 없습니다");
        }
    }

    private void validatePokeOrganizationMembers(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember
    ) {
        Assert.notNull(sendOrganizationMember, "포키를 보내는 조직원은 null이 되면 안됩니다.");
        Assert.notNull(receiveOrganizationMember, "포키를 받는 조직원은 null이 되면 안됩니다.");

        if (sendOrganizationMember.equals(receiveOrganizationMember)) {
            throw new BusinessRuleViolatedException("스스로에게 포키를 보낼 수 없습니다");
        }
    }

    private void validateOrganizationParticipate(
            final Event event,
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember
    ) {
        Organization organization = event.getOrganization();

        if (!organization.isExistOrganizationMember(sendOrganizationMember)) {
            throw new BusinessRuleViolatedException("포키를 보내려면 해당 조직에 참여하고 있어야 합니다.");
        }

        if (!organization.isExistOrganizationMember(receiveOrganizationMember)) {
            throw new BusinessRuleViolatedException("포키 대상이 해당 조직에 참여하고 있어야 합니다.");
        }
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트는 null이 되면 안됩니다.");
    }
}
