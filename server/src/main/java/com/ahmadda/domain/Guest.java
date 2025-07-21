package com.ahmadda.domain;


import com.ahmadda.domain.exception.BusinessRuleViolatedException;
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

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private OrganizationMember organizationMember;

    private Guest(final Event event, final OrganizationMember organizationMember, final LocalDateTime currentDateTime) {
        validateEvent(event);
        validateOrganizationMember(organizationMember);
        validateSameOrganization(event, organizationMember);

        this.event = event;
        this.organizationMember = organizationMember;

        event.participate(this, currentDateTime);
    }

    public static Guest create(
            final Event event,
            final OrganizationMember organizationMember,
            final LocalDateTime currentDateTime
    ) {
        return new Guest(event, organizationMember, currentDateTime);
    }

    public boolean isSameOrganizationMember(final OrganizationMember organizationMember) {
        return this.organizationMember.equals(organizationMember);
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트는 null이 되면 안됩니다.");
    }

    private void validateOrganizationMember(final OrganizationMember organizationMember) {
        Assert.notNull(organizationMember, "참여자는 null이 되면 안됩니다.");
    }

    private void validateSameOrganization(final Event event, final OrganizationMember organizationMember) {
        if (!organizationMember.isBelongTo(event.getOrganization())) {
            throw new BusinessRuleViolatedException("같은 조직의 이벤트에만 게스트로 참여가능합니다.");
        }
    }
}
