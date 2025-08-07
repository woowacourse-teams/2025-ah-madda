package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreated;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
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
public class EventCreatedListener {

    private final EventStatisticRepository eventStatisticRepository;
    private final EventRepository eventRepository;

    @EventListener
    @Transactional
    public void onEventCreated(final EventCreated eventCreated) {
        Event event = eventRepository.findById(eventCreated.eventId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));

        eventStatisticRepository.save(EventStatistic.create(event));
    }
}
