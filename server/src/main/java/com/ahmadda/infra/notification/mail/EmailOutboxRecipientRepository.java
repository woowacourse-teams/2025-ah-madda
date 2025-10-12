package com.ahmadda.infra.notification.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface EmailOutboxRecipientRepository extends JpaRepository<EmailOutboxRecipient, Long> {

    @Modifying(clearAutomatically = true)
    int deleteByEmailOutboxIdAndRecipientEmail(final Long emailOutboxId, final String recipientEmail);

    boolean existsByEmailOutboxId(final Long emailOutboxId);

    List<EmailOutboxRecipient> findAllByEmailOutboxId(final Long emailOutboxId);
}
