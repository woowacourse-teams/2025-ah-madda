package com.ahmadda.domain.event;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.organization.OrganizationMember;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE event_statistic SET deleted_at = CURRENT_TIMESTAMP WHERE event_statistic_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class EventStatistic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_statistic_id")
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_statistic_id", nullable = false)
    private final List<EventViewMetric> eventViewMetrics = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", unique = true, nullable = false)
    private Event event;

    private EventStatistic(final Event event) {
        this.event = event;

        updateEventViewMatricUntilEventEnd();
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

    private void validateIsOrganizer(final OrganizationMember organizationMember) {
        if (!event.isOrganizer(organizationMember)) {
            throw new ForbiddenException("이벤트의 조회수는 이벤트의 주최자만 조회할 수 있습니다.");
        }
    }

    public void updateEventViewMatricUntilEventEnd() {
        LocalDate currentDate = LocalDate.from(event.getEventOperationPeriod()
                .getRegistrationEventPeriod()
                .start());

        EventOperationPeriod eventOperationPeriod = event.getEventOperationPeriod();

        Set<LocalDate> existViewMetricDates = calculateExistViewMetricDate();

        while (!eventOperationPeriod.isAfterEventEndDate(currentDate)) {
            if (!existViewMetricDates.contains(currentDate)) {
                EventViewMetric eventViewMetric = EventViewMetric.create(currentDate);
                eventViewMetrics.add(eventViewMetric);
                existViewMetricDates.add(currentDate);
            }

            currentDate = currentDate.plusDays(1L);
        }
    }

    private Set<LocalDate> calculateExistViewMetricDate() {
        Set<LocalDate> localDates = new HashSet<>();
        for (EventViewMetric eventViewMetric : eventViewMetrics) {
            localDates.add(eventViewMetric.getViewDate());
        }

        return localDates;
    }
}
