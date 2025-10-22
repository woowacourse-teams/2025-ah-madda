package com.ahmadda.infra.notification.mail.outbox;

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
    private String subject;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String body;

    @Column(nullable = false)
    private LocalDateTime lockedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private EmailOutbox(
            final String subject,
            final String body,
            final LocalDateTime lockedAt,
            final LocalDateTime createdAt
    ) {
        this.subject = subject;
        this.body = body;
        this.lockedAt = lockedAt;
        this.createdAt = createdAt;
    }

    public static EmailOutbox create(
            final String subject,
            final String body,
            final LocalDateTime lockedAt,
            final LocalDateTime createdAt
    ) {
        return new EmailOutbox(subject, body, lockedAt, createdAt);
    }

    public static EmailOutbox createNow(final String subject, final String body) {
        LocalDateTime now = LocalDateTime.now();

        return new EmailOutbox(subject, body, now, now);
    }

    public void lock() {
        this.lockedAt = LocalDateTime.now();
    }
}
