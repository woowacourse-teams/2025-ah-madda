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
        this.title = Assert.notNull(title, "title null이 되면 안됩니다.");
        this.description = Assert.notNull(description, "description null이 되면 안됩니다.");
        this.place = Assert.notNull(place, "place null이 되면 안됩니다.");
        this.organizer = Assert.notNull(organizer, "organizer null이 되면 안됩니다.");
        this.organization = Assert.notNull(organization, "organization null이 되면 안됩니다.");
        this.registrationStart = Assert.notNull(registrationStart, "registrationStart null이 되면 안됩니다.");
        this.registrationEnd = Assert.notNull(registrationEnd, "registrationEnd null이 되면 안됩니다.");
        this.eventStart = Assert.notNull(eventStart, "eventStart null이 되면 안됩니다.");
        this.eventEnd = Assert.notNull(eventEnd, "eventEnd null이 되면 안됩니다.");
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


