package com.ahmadda.domain;

import com.ahmadda.domain.exception.UnauthorizedOperationException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventStatistic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_statistic_id")
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_statistic_id", nullable = false)
    private List<EventViewMetric> eventViewMetrics = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", unique = true, nullable = false)
    private Event event;

    private EventStatistic(final Event event) {
        validateEvent(event);

        this.event = event;

        createEventViewMatricUntilEventEnd(event);
    }

    public static EventStatistic create(final Event event) {
        return new EventStatistic(event);
    }

    public void increaseViewCount(final LocalDate currentDate) {
        eventViewMetrics.stream()
                .filter((eventViewMetric) -> eventViewMetric.isSameDate(currentDate))
                .findFirst()
                .ifPresent(EventViewMetric::increaseViewCount);
    }

    public List<EventViewMetric> findEventViewMetrics(
            final OrganizationMember organizationMember,
            final LocalDate currentDate
    ) {
        validateIsOrganizer(organizationMember);

        return eventViewMetrics.stream()
                .filter((eventViewMetric) -> !eventViewMetric.isAfter(currentDate))
                .toList();
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트 조회수의 이벤트가 null일 수 없습니다.");
    }

    private void validateIsOrganizer(final OrganizationMember organizationMember) {
        if (!event.getOrganizer()
                .equals(organizationMember)) {
            throw new UnauthorizedOperationException("이벤트의 조회수는 이벤트의 주최자만 조회할 수 있습니다.");
        }
    }

    private void createEventViewMatricUntilEventEnd(final Event event) {
        LocalDate currentDate = LocalDate.from(
                event.getEventOperationPeriod()
                        .getRegistrationPeriod()
                        .start());
        EventOperationPeriod eventOperationPeriod = event.getEventOperationPeriod();

        while (!eventOperationPeriod.isAfterEventEndDate(currentDate)) {
            EventViewMetric eventViewMetric = EventViewMetric.create(currentDate);
            eventViewMetrics.add(eventViewMetric);
            currentDate = currentDate.plusDays(1L);
        }
    }
}
