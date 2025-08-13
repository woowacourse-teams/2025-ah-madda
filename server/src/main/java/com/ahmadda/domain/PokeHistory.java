package com.ahmadda.domain;

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
public class PokeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    private PokeHistory(
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final Event event,
            final LocalDateTime sentAt
    ) {
        this.sender = sender;
        this.recipient = recipient;
        this.event = event;
        this.sentAt = sentAt;
    }

    public static PokeHistory create(
            final OrganizationMember sendOrganizationMember,
            final OrganizationMember receiveOrganizationMember,
            final Event event,
            final LocalDateTime dateTime
    ) {
        return new PokeHistory(sendOrganizationMember, receiveOrganizationMember, event, dateTime);
    }
}
