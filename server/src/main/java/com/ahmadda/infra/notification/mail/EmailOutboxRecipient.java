package com.ahmadda.infra.notification.mail;

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
public class EmailOutboxRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_outbox_recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_outbox_id", nullable = false)
    private EmailOutbox emailOutbox;

    @Column(nullable = false)
    private String recipientEmail;

    private EmailOutboxRecipient(final EmailOutbox emailOutbox, final String recipientEmail) {
        this.emailOutbox = emailOutbox;
        this.recipientEmail = recipientEmail;
    }

    public static EmailOutboxRecipient create(final EmailOutbox emailOutbox, final String recipientEmail) {
        return new EmailOutboxRecipient(emailOutbox, recipientEmail);
    }
}
