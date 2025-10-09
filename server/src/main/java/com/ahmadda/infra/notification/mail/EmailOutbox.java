package com.ahmadda.infra.notification.mail;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "emailOutbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<EmailOutboxRecipient> recipients = new ArrayList<>();

    @Column
    private LocalDateTime lockedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private EmailOutbox(final String subject, final String body, final LocalDateTime createdAt) {
        this.subject = subject;
        this.body = body;
        this.createdAt = createdAt;
    }

    public static EmailOutbox createNow(final String subject, final String body, final List<String> recipientEmails) {
        EmailOutbox outbox = new EmailOutbox(subject, body, LocalDateTime.now());
        recipientEmails.forEach(email -> outbox.recipients.add(EmailOutboxRecipient.create(outbox, email)));

        return outbox;
    }

    public void lock() {
        this.lockedAt = LocalDateTime.now();
    }

    public boolean isLockExpired(final int ttlMinutes) {
        return lockedAt == null || lockedAt.isBefore(LocalDateTime.now()
                .minusMinutes(ttlMinutes));
    }

    public boolean isAllRecipientsDeleted() {
        return recipients.isEmpty();
    }
}
