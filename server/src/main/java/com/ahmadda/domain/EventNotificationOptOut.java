package com.ahmadda.domain;

import com.ahmadda.domain.util.Assert;
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
        validateOrganizationMember(organizationMember);
        validateEvent(event);

        this.organizationMember = organizationMember;
        this.event = event;
    }

    public static EventNotificationOptOut create(
            final OrganizationMember organizationMember,
            final Event event
    ) {
        return new EventNotificationOptOut(organizationMember, event);
    }

    private void validateOrganizationMember(final OrganizationMember organizationMember) {
        Assert.notNull(organizationMember, "알림 수신 거부의 조직원은 null이 될 수 없습니다.");
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "알림 수신 거부의 이벤트는 null이 될 수 없습니다.");
    }
}
