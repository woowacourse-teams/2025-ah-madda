package com.ahmadda.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventViewMetric extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_view_metric_id")
    private Long id;

    private LocalDate viewDate;

    private Integer viewCount;

    public EventViewMetric(final LocalDate viewDate) {
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
