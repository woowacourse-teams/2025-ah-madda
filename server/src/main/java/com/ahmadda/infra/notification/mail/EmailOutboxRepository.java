package com.ahmadda.infra.notification.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    Optional<EmailOutbox> findBySubjectAndBody(final String subject, final String body);

    /**
     * 지정된 시각(threshold) 이전에 locked_at이 만료된 Outbox 레코드를 조회하고 잠근다.
     * FOR UPDATE SKIP LOCKED로 병렬 처리 시 중복을 방지하며,
     * ORDER BY id LIMIT 50으로 넥스트키 락을 피하고 트랜잭션 락 범위를 제한한다.
     *
     * @param threshold 잠금 만료 기준 시각
     * @return 잠금 만료된 Outbox 레코드 목록 (최대 50건)
     */
    @Query(value = """
            select *
            from email_outbox o
            where o.locked_at < :threshold
            order by o.email_outbox_id
            limit 50
            for update skip locked
            """, nativeQuery = true)
    List<EmailOutbox> findAndLockExpiredOutboxes(final LocalDateTime threshold);
}
