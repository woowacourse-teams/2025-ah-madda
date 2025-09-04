package com.ahmadda.application;

import com.ahmadda.application.dto.EventUpdated;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatistic;
import com.ahmadda.domain.event.EventStatisticRepository;
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
    private final EventRepository eventRepository;

    @EventListener
    @Transactional
    public void onEventUpdated(final EventUpdated eventUpdated) {
        eventStatisticRepository.findByEventId(eventUpdated.eventId())
                .ifPresentOrElse(
                        EventStatistic::updateEventViewMatricUntilEventEnd,
                        () -> {
                            Event event = eventRepository.findById(eventUpdated.eventId())
                                    .orElseThrow((() -> new NotFoundException("존재하지 않는 이벤트입니다")));
                            EventStatistic eventStatistic = EventStatistic.create(event);

                            eventStatisticRepository.save(eventStatistic);
                        }
                );
    }
}
