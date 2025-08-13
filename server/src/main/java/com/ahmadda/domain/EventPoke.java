package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventPoke extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계가 더 자연스럽습니다.
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private OrganizationMember sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private OrganizationMember recipient;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private EventPoke(
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

        this.sender = sender;
        this.recipient = recipient;
        this.event = event;
        this.sentAt = sentAt;
    }

    public static EventPoke create(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember,
            final Event event,
            final LocalDateTime dateTime
    ) {
        return new EventPoke(sendOrganizationMember, receiveOrganizationMember, event, dateTime);
    }

    private void validateSentAt(final LocalDateTime sentAt) {
        Assert.notNull(sentAt, "포크 전송 날짜는 null 일 수 없습니다.");
    }

    private void validateReceiveOrganizationMember(
            final Event event,
            final OrganizationMember receiveOrganizationMember
    ) {
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
