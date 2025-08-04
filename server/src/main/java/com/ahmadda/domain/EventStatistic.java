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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventStatistic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_statistic_id")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "event_id", unique = true)
    private Event event;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_statistic_id")
    private final List<EventViewMetric> eventViewMetrics = new ArrayList<>();

    private EventStatistic(final Event event, LocalDateTime createdDatetime) {
        validateEvent(event);

        this.event = event;

        createEventViewMatricUntilEventEnd(event, createdDatetime);
    }

    public static EventStatistic create(final Event event, final LocalDateTime createdDatetime) {
        return new EventStatistic(event, createdDatetime);
    }

    public List<EventViewMetric> getEventViewMetrics(OrganizationMember organizationMember,
                                                     LocalDateTime currentDateTime) {
        validateAccess(organizationMember);

        return Collections.unmodifiableList(
                eventViewMetrics.stream()
                        .filter((eventViewMetric) -> !eventViewMetric.isAfter(currentDateTime))
                        .toList()
        );
    }

    private static void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트 조회수의 이벤트가 null일 수 없습니다.");
    }

    private void validateAccess(OrganizationMember organizationMember) {
        if (!event.getOrganizer().equals(organizationMember)) {
            throw new UnauthorizedOperationException("이벤트의 조회수는 이벤트의 주최자만 참조할 수 있습니다.");
        }
    }

    private void createEventViewMatricUntilEventEnd(final Event event, LocalDateTime createdDatetime) {
        LocalDateTime currentDate = createdDatetime;
        EventOperationPeriod eventOperationPeriod = event.getEventOperationPeriod();

        while (eventOperationPeriod.isBeforeEventEnd(currentDate)) {
            EventViewMetric eventViewMetric = EventViewMetric.create(currentDate);
            eventViewMetrics.add(eventViewMetric);
            currentDate = currentDate.plusDays(1L);
        }
    }
}
