package com.ahmadda.domain.notification;

import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.organization.OrganizationMember;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE reminder_history SET deleted_at = CURRENT_TIMESTAMP WHERE reminder_history_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ReminderHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reminder_history_id", nullable = false)
    private final List<ReminderRecipient> recipients = new ArrayList<>();

    private ReminderHistory(
            final Event event,
            final String content,
            final LocalDateTime sentAt,
            final List<OrganizationMember> organizationMembers
    ) {
        this.event = event;
        this.content = content;
        this.sentAt = sentAt;
        organizationMembers.forEach(organizationMember ->
                                            this.recipients.add(ReminderRecipient.create(organizationMember))
        );
    }

    public static ReminderHistory create(
            final Event event,
            final String content,
            final LocalDateTime sentAt,
            final List<OrganizationMember> organizationMembers
    ) {
        return new ReminderHistory(event, content, sentAt, organizationMembers);
    }

    public static ReminderHistory createNow(
            final Event event,
            final String content,
            final List<OrganizationMember> organizationMembers
    ) {
        return new ReminderHistory(event, content, LocalDateTime.now(), organizationMembers);
    }
}
