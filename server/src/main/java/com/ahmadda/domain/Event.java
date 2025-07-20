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
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 2_100_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private OrganizationMember organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    private List<Guest> guests;

    @Column(nullable = false)
    private EventOperationPeriod eventOperationPeriod;

    @Column(nullable = false)
    private int maxCapacity;

    private Event(
            final String title,
            final String description,
            final String place,
            final OrganizationMember organizer,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity
    ) {
        validateTitle(title);
        validateDescription(description);
        validatePlace(place);
        validateOrganizer(organizer);
        validateOrganization(organization);
        validateBelongToOrganization(organizer, organization);
        validateMaxCapacity(maxCapacity);

        this.title = title;
        this.description = description;
        this.place = place;
        this.organizer = organizer;
        this.organization = organization;
        this.eventOperationPeriod = eventOperationPeriod;
        this.maxCapacity = maxCapacity;
        guests = new ArrayList<>();

        organization.addEvent(this);
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final OrganizationMember organizer,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity
    ) {
        return new Event(
                title,
                description,
                place,
                organizer,
                organization,
                eventOperationPeriod,
                maxCapacity
        );
    }

    public boolean hasGuest(final OrganizationMember organizationMember) {
        return guests.stream()
                .anyMatch(guest -> guest.isSameParticipant(organizationMember));
    }

    private void validateTitle(final String title) {
        Assert.notBlank(title, "제목은 공백이면 안됩니다.");
    }

    private void validateDescription(final String description) {
        Assert.notBlank(description, "설명은 공백이면 안됩니다.");
    }

    private void validatePlace(final String place) {
        Assert.notBlank(place, "장소는 공백이면 안됩니다.");
    }

    private void validateOrganizer(final OrganizationMember organizer) {
        Assert.notNull(organizer, "주최자는 null이 되면 안됩니다.");
    }

    private void validateOrganization(final Organization organization) {
        Assert.notNull(organization, "조직은 null이 되면 안됩니다.");
    }

    private void validateBelongToOrganization(OrganizationMember organizer, Organization organization) {
        if (!organizer.isBelongTo(organization)) {
            throw new BusinessRuleViolatedException("자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.");
        }
    }

    private void validateMaxCapacity(final int maxCapacity) {
        if (maxCapacity < MIN_CAPACITY || maxCapacity > MAX_CAPACITY) {
            throw new BusinessRuleViolatedException("최대 수용 인원은 1명보다 적거나 21억보다 클 수 없습니다.");
        }
    }
}
