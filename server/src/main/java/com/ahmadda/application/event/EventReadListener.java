package com.ahmadda.application.event;

import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventReadListener {

    private final EventStatisticRepository eventStatisticRepository;

    @EventListener
    public void onEventCreated(final EventRead eventRead) {
        EventStatistic eventStatistic = eventStatisticRepository.findByEventId(eventRead.eventId())
                .orElseThrow(() -> new NotFoundException("해당되는 이벤트의 조회수를 가져오는데 실패하였습니다."));
        eventStatistic.increaseViewCount(LocalDate.now());
    }
}
