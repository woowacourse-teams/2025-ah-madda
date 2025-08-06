package com.ahmadda.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventViewMetric extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_view_metric_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate viewDate;

    @Column(nullable = false)
    private int viewCount;

    private EventViewMetric(final LocalDate viewDate) {
        this.viewDate = viewDate;
        viewCount = 0;
    }

    public static EventViewMetric create(final LocalDate viewDate) {
        return new EventViewMetric(viewDate);
    }

    public void increaseViewCount() {
        viewCount++;
    }

    public boolean isAfter(final LocalDate currentDate) {
        return viewDate.isAfter(currentDate);
    }

    public boolean isSameDate(final LocalDate currentDate) {
        return viewDate.isEqual(currentDate);
    }
}
