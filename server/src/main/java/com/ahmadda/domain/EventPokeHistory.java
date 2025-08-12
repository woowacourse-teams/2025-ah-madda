package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class EventPokeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_poke_history_id", unique = true, nullable = false)
    private OrganizationMember sender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_poke_history_id", unique = true, nullable = false)
    private OrganizationMember receiver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_poke_history_id", unique = true, nullable = false)
    private Event event;

    private EventPokeHistory(
            final OrganizationMember sender,
            final OrganizationMember receiver,
            final Event event
    ) {
        validateEvent(event);
        validatePokeOrganizationMembers(sender, receiver);
        validateOrganizationParticipate(event, sender, receiver);
        validateReceiveOrganizationMember(event, receiver);

        this.sender = sender;
        this.receiver = receiver;
        this.event = event;
    }

    protected EventPokeHistory() {

    }

    public static EventPokeHistory create(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember,
            final Event event
    ) {
        return new EventPokeHistory(sendOrganizationMember, receiveOrganizationMember, event);
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

        if (sendOrganizationMember.getId()
                .equals(receiveOrganizationMember.getId())) {
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
