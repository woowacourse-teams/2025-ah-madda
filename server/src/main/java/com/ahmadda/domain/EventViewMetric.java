package com.ahmadda.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventViewMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_view_metric_id")
    private Long id;

    private LocalDateTime viewDate;

    private Integer viewCount;

    public EventViewMetric(final LocalDateTime viewDate) {
        this.viewDate = viewDate;
        viewCount = 0;
    }

    public static EventViewMetric create(final LocalDateTime viewDate) {
        return new EventViewMetric(viewDate);
    }

    public boolean isAfter(LocalDateTime currentDateTime) {
        return viewDate.isAfter(currentDateTime);
    }
}

