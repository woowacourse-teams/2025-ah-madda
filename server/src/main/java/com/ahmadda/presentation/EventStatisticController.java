package com.ahmadda.presentation;

import com.ahmadda.application.EventStatisticService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.EventViewMetric;
import com.ahmadda.presentation.dto.EventViewMetricResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/statistic")
public class EventStatisticController {

    private final EventStatisticService eventStatisticService;

    @GetMapping("/")
    public ResponseEntity<List<EventViewMetricResponse>> eventStatisticResponses(
            @PathVariable final Long eventId,
            @AuthMember LoginMember loginMember
    ){
        List<EventViewMetric> eventStatistics = eventStatisticService.getEventStatistic(eventId,loginMember);

        List<EventViewMetricResponse> metricResponses = eventStatistics.stream()
                .map((EventViewMetricResponse::from))
                .toList();

        return ResponseEntity.ok(metricResponses);
    }
}
