package com.ahmadda.infra.notification.mail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    Optional<EmailOutbox> findByEventIdAndRecipientEmailAndContent(Long eventId, String recipientEmail, String content);
}
