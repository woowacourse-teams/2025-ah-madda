package com.ahmadda.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
                  final int maxCapacity) {
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
    }

    public static Event create(final String title,
                               final String description,
                               final String place,
                               final OrganizationMember organizer,
                               final Organization organization,
                               final LocalDateTime registrationStart,
                               final LocalDateTime registrationEnd,
                               final LocalDateTime eventStart,
                               final LocalDateTime eventEnd,
                               final int maxCapacity) {
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
                maxCapacity);
    }
}


