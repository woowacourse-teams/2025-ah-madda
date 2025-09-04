package com.ahmadda.domain.notification;

import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.organization.OrganizationMember;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE event_notification_opt_out SET deleted_at = CURRENT_TIMESTAMP WHERE event_notification_opt_out_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class EventNotificationOptOut extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_notification_opt_out_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_member_id", nullable = false)
    private OrganizationMember organizationMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private EventNotificationOptOut(
            final OrganizationMember organizationMember,
            final Event event
    ) {
        this.organizationMember = organizationMember;
        this.event = event;
    }

    public static EventNotificationOptOut create(
            final OrganizationMember organizationMember,
            final Event event
    ) {
        return new EventNotificationOptOut(organizationMember, event);
    }
}
