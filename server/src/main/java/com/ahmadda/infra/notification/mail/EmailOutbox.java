package com.ahmadda.infra.notification.mail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_outbox_id")
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastAttemptAt;

    @Column
    private String failReason;

    private EmailOutbox(
            final Long eventId,
            final String recipientEmail,
            final String content,
            final String status,
            final LocalDateTime createdAt
    ) {
        this.eventId = eventId;
        this.recipientEmail = recipientEmail;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static EmailOutbox createNow(
            final Long eventId,
            final String recipientEmail,
            final String content
    ) {
        return new EmailOutbox(eventId, recipientEmail, content, Status.PENDING.name(), LocalDateTime.now());
    }

    public void markSent() {
        this.status = Status.SENT.name();
        this.lastAttemptAt = LocalDateTime.now();
    }

    public void markFailed(final String reason) {
        this.status = Status.FAILED.name();
        this.failReason = reason;
        this.lastAttemptAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING,
        SENT,
        FAILED
    }
}
