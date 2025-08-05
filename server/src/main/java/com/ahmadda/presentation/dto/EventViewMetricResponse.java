package com.ahmadda.presentation.dto;

import com.ahmadda.domain.EventViewMetric;
import java.time.LocalDate;

public record EventViewMetricResponse(LocalDate date,int count) {

    public static EventViewMetricResponse from(final EventViewMetric eventViewMetric) {
        return new EventViewMetricResponse(eventViewMetric.getViewDate(),eventViewMetric.getViewCount());
    }
}
