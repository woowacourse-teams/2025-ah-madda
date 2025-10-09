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

    private EmailOutbox(
            final String subject,
            final String body,
            final List<String> recipientEmails,
            final LocalDateTime lockedAt,
            final LocalDateTime createdAt
    ) {
        this.subject = subject;
        this.body = body;
        recipientEmails.forEach(recipientEmail ->
                this.recipients.add(EmailOutboxRecipient.create(this, recipientEmail))
        );
        this.lockedAt = lockedAt;
        this.createdAt = createdAt;
    }

    public static EmailOutbox create(
            final String subject,
            final String body,
            final List<String> recipientEmails,
            final LocalDateTime lockedAt,
            final LocalDateTime createdAt
    ) {
        return new EmailOutbox(subject, body, recipientEmails, lockedAt, createdAt);
    }

    public static EmailOutbox createNow(final String subject, final String body, final List<String> recipientEmails) {
        LocalDateTime now = LocalDateTime.now();
        return new EmailOutbox(subject, body, recipientEmails, now, now);
    }

    public void lock() {
        this.lockedAt = LocalDateTime.now();
    }
}
