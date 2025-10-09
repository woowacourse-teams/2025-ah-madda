package com.ahmadda.infra.notification.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    @Query(value = """
            select *
            from email_outbox o
            where (o.locked_at is null or o.locked_at < :threshold)
            order by o.created_at asc
            for update skip locked
            """, nativeQuery = true)
    List<EmailOutbox> findAndLockExpiredOutboxes(@Param("threshold") final LocalDateTime threshold);
}
