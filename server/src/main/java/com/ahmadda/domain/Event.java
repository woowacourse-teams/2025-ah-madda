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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

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
    private LocalDateTime registrationStart;

    @Column(nullable = false)
    private LocalDateTime registrationEnd;

    @Column(nullable = false)
    private LocalDateTime eventStart;

    @Column(nullable = false)
    private LocalDateTime eventEnd;

    @Column(nullable = false)
    private int maxCapacity;

    private Event(final String title,
                  final String description,
                  final String place,
                  final OrganizationMember organizer,
                  final Organization organization,
                  final LocalDateTime registrationStart,
                  final LocalDateTime registrationEnd,
                  final LocalDateTime eventStart,
                  final LocalDateTime eventEnd,
                  final int maxCapacity
    ) {
        validateTitle(title);
        validateDescription(description);
        validatePlace(place);
        validateOrganizer(organizer);
        validateOrganization(organization);
        validateRegistrationStart(registrationStart);
        validateRegistrationEnd(registrationEnd);
        validateEventStart(eventStart);
        validateEventEnd(eventEnd);
        validateBelongToOrganization(organizer, organization);

        this.title = title;
        this.description = description;
        this.place = place;
        this.organizer = organizer;
        this.organization = organization;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.maxCapacity = maxCapacity;
        guests = new ArrayList<>();

        organization.addEvent(this);
        validateTitle(title);
        validateBelongToOrganization(organizer, organization);
    }

    private void validateBelongToOrganization(OrganizationMember organizer, Organization organization) {
        if (!organizer.isBelongTo(organization)) {
            throw new BusinessRuleViolatedException("자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.");
        }
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final OrganizationMember organizer,
            final Organization organization,
            final LocalDateTime registrationStart,
            final LocalDateTime registrationEnd,
            final LocalDateTime eventStart,
            final LocalDateTime eventEnd,
            final int maxCapacity,
            final LocalDateTime currentDateTime
    ) {
        if (eventStart.isBefore(currentDateTime)) {
            throw new BusinessRuleViolatedException("이벤트 시작 시간은 이벤트 생성 요청 시점보다 과거일 수 없습니다.");
        }
        if (eventEnd.equals(eventStart) || eventEnd.isBefore(eventStart)) {
            throw new BusinessRuleViolatedException("이벤트 종료 시간은 이벤트 시작 시간보다 과거 이거나 같을 수 없습니다.");
        }
        if (registrationStart.isBefore(currentDateTime)) {
            throw new BusinessRuleViolatedException("이벤트 신청 시작 시간은 이벤트 생성 요청 시점보다 과거일 수 없습니다.");
        }
        if (registrationStart.equals(eventStart) || registrationStart.isAfter(eventStart)) {
            throw new BusinessRuleViolatedException("이벤트 신청 시작 시간은 이벤트 시작 시간보다 과거여야 합니다.");
        }
        if (registrationEnd.equals(registrationStart) || registrationEnd.isBefore(registrationStart)) {
            throw new BusinessRuleViolatedException("이벤트 신청 마감 시간은 이벤트 신청 시작 시간보다 미래여야 합니다.");
        }
        if (registrationEnd.equals(eventStart) || registrationEnd.isAfter(eventStart)) {
            throw new BusinessRuleViolatedException("이벤트 신청 마감 시간은 이벤트 시작 시간보다 미래일 수 없습니다.");
        }
        if (maxCapacity < 1 || maxCapacity > 2_100_000_000) {
            throw new BusinessRuleViolatedException("최대 수용 인원은 1명보다 적거나 21억보다 클 수 없습니다.");
        }

        return new Event(
                title,
                description,
                place,
                organizer,
                organization,
                registrationStart,
                registrationEnd,
                eventStart,
                eventEnd,
                maxCapacity
        );
    }

    public boolean hasGuest(final OrganizationMember organizationMember) {
        return guests.stream()
                .anyMatch(guest -> guest.isSameParticipant(organizationMember));
    }

    private void validateTitle(final String title) {
        Assert.notBlank(title, "title은 공백이면 안됩니다.");

        if (title.length() > 255) {
            throw new BusinessRuleViolatedException("title은 최소 1글자 최대 255글자여야 합니다.");
        }
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

    private void validateRegistrationStart(final LocalDateTime registrationStart) {
        Assert.notNull(registrationStart, "신청 시작 시간은 null이 되면 안됩니다.");
    }

    private void validateRegistrationEnd(final LocalDateTime registrationEnd) {
        Assert.notNull(registrationEnd, "신청 마감 시간은 null이 되면 안됩니다.");
    }

    private void validateEventStart(final LocalDateTime eventStart) {
        Assert.notNull(eventStart, "시작 시간은 null이 되면 안됩니다.");
    }

    private void validateEventEnd(final LocalDateTime eventEnd) {
        Assert.notNull(eventEnd, "종료 시간은 null이 되면 안됩니다.");
    }
}
