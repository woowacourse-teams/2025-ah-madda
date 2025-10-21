package com.ahmadda.domain.notification;

import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Poke {

    private static final int MAX_SENDABLE_COUNT = 3;
    private static final Duration DUPLICATE_POKE_COUNT_MINUTES = Duration.ofMinutes(30L);

    private final PushNotifier pushNotifier;
    private final PokeHistoryRepository pokeHistoryRepository;

    @Transactional
    public PokeHistory doPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final PokeMessage pokeMessage,
            final Event event,
            final LocalDateTime sentAt
    ) {
        validateDoPoke(sender, recipient, event, sentAt);
        validateDuplicateDoPoke(sender, recipient, event, sentAt);

        pushPoke(sender, recipient, pokeMessage, event);

        return PokeHistory.create(sender, recipient, event, sentAt);
    }

    private void validateDuplicateDoPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime sentAt
    ) {
        LocalDateTime findDuplicateStartTime = sentAt.minus(DUPLICATE_POKE_COUNT_MINUTES);

        List<PokeHistory> pokeHistories = getRecentPokeHistories(sender, recipient, event, findDuplicateStartTime);

        int count = pokeHistories.size();
        if (count >= MAX_SENDABLE_COUNT) {
            long minutes = getRemainMinutesForPoke(findDuplicateStartTime, pokeHistories);
            throw new UnprocessableEntityException(String.format(
                    "%s님에게 너무 많은 포키를 보냈어요 🫠 %d분 뒤에 찌를 수 있어요!",
                    recipient.getNickname(),
                    minutes
            ));
        }
    }

    private long getRemainMinutesForPoke(final LocalDateTime currentSentAt, final List<PokeHistory> pokeHistories) {
        pokeHistories.sort(Comparator.comparing(PokeHistory::getSentAt));
        LocalDateTime mostOldSentAt = pokeHistories.getFirst()
                .getSentAt();

        return ChronoUnit.MINUTES.between(currentSentAt, mostOldSentAt);
    }

    private List<PokeHistory> getRecentPokeHistories(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime findDuplicateStartTime
    ) {
        return pokeHistoryRepository.findAllByEventAndSenderAndRecipientAndSentAtAfter(
                event,
                sender,
                recipient,
                findDuplicateStartTime
        );
    }

    private void validateDoPoke(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime sentAt
    ) {
        validatePokeOrganizationMembers(sender, recipient);
        validateOrganizationParticipate(event, sender, recipient);
        validateReceiveOrganizationMember(event, recipient);
    }

    private void pushPoke(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember,
            final PokeMessage pokeMessage,
            final Event event
    ) {
        String sendMessage = pokeMessage.getMessage(sendOrganizationMember.getNickname());

        pushNotifier.poke(receiveOrganizationMember, PushNotificationPayload.of(event, sendMessage));
    }

    private void validateReceiveOrganizationMember(
            final Event event,
            final OrganizationMember receiveOrganizationMember
    ) {
        if (event.hasGuest(receiveOrganizationMember)) {
            throw new UnprocessableEntityException("이미 이벤트에 참여한 구성원에게 포키를 보낼 수 없습니다.");
        }

        if (event.isOrganizer(receiveOrganizationMember)) {
            throw new UnprocessableEntityException("주최자에게 포키를 보낼 수 없습니다");
        }
    }

    private void validatePokeOrganizationMembers(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember
    ) {
        if (sendOrganizationMember.equals(receiveOrganizationMember)) {
            throw new UnprocessableEntityException("스스로에게 포키를 보낼 수 없습니다");
        }
    }

    private void validateOrganizationParticipate(
            final Event event,
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember
    ) {
        Organization organization = event.getOrganization();

        if (!sendOrganizationMember.isBelongTo(organization)) {
            throw new UnprocessableEntityException("포키를 보내려면 해당 이벤트 스페이스에 참여하고 있어야 합니다.");
        }

        if (!receiveOrganizationMember.isBelongTo(organization)) {
            throw new UnprocessableEntityException("포키 대상이 해당 이벤트 스페이스에 참여하고 있어야 합니다.");
        }
    }
}
