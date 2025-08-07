package com.ahmadda.application;

import com.ahmadda.application.dto.EventUpdated;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventUpdateListener {

    private final EventStatisticRepository eventStatisticRepository;

    @EventListener
    @Transactional
    public void onEventUpdated(final EventUpdated eventUpdated) {
        EventStatistic eventStatistic = eventStatisticRepository.findByEventId(eventUpdated.eventId())
                .orElseThrow(() -> new NotFoundException("해당되는 이벤트의 조회수를 가져오는데 실패하였습니다."));

        eventStatistic.updateEventViewMatricUntilEventEnd();
    }
}
