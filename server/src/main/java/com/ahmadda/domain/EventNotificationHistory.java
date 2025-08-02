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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventNotificationHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_notification_history_id")
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
    private String content;

    private EventNotificationHistory(
            final Event event,
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final String content
    ) {
        validateEvent(event);
        validateSender(sender);
        validateRecipient(recipient);
        validateContent(content);

        this.event = event;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public static EventNotificationHistory create(
            final Event event,
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final String content
    ) {
        return new EventNotificationHistory(event, sender, recipient, content);
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트는 null일 수 없습니다.");
    }

    private void validateSender(final OrganizationMember sender) {
        Assert.notNull(sender, "발신자는 null일 수 없습니다.");
    }

    private void validateRecipient(final OrganizationMember recipient) {
        Assert.notNull(recipient, "수신자는 null일 수 없습니다.");
    }

    private void validateContent(final String content) {
        Assert.notBlank(content, "알림 내용은 공백일 수 없습니다.");
    }
}
