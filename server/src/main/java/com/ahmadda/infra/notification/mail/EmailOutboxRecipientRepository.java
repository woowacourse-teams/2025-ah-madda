package com.ahmadda.infra.notification.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmailOutboxRecipientRepository extends JpaRepository<EmailOutboxRecipient, Long> {

    @Modifying
    @Query("""
                delete from EmailOutboxRecipient r
                where r.recipientEmail = :recipientEmail
                  and r.emailOutbox.subject = :subject
                  and r.emailOutbox.body = :body
            """)
    void deleteByRecipientEmailAndSubjectAndBody(final String recipientEmail, final String subject, final String body);
}
