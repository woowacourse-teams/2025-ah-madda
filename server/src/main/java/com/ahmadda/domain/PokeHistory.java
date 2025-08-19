package com.ahmadda.domain;

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
public class PokeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poke_history_id")
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
        validateOrganizationMember(sender, recipient);
        validateEvent(event);
        validateSentAt(sentAt);

        this.sender = sender;
        this.recipient = recipient;
        this.event = event;
        this.sentAt = sentAt;
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트는 null일 수 없습니다.");
    }

    private void validateSentAt(final LocalDateTime sentAt) {
        Assert.notNull(sentAt, "전송 시간은 null일 수 없습니다.");
    }

    private void validateOrganizationMember(final OrganizationMember sender, final OrganizationMember recipient) {
        Assert.notNull(sender, "포키 전송자는 null일 수 없습니다.");
        Assert.notNull(recipient, "포키 수신자는 null일 수 없습니다.");
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
