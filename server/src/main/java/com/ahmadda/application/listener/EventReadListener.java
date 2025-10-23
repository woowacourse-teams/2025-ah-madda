package com.ahmadda.application.listener;

import com.ahmadda.application.dto.EventRead;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatistic;
import com.ahmadda.domain.event.EventStatisticRepository;
import com.ahmadda.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventReadListener {

    private final EventStatisticRepository eventStatisticRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @EventListener
    @Transactional
    public void onEventRead(final EventRead eventRead) {
        eventStatisticRepository.findByEventId(eventRead.eventId())
                .ifPresentOrElse(
                        this::increaseViewCount,
                        () -> {
                            Event event = eventRepository.findById(eventRead.eventId())
                                    .orElseThrow((() -> new NotFoundException("존재하지 않는 이벤트입니다")));

                            EventStatistic eventStatistic = EventStatistic.create(event);

                            increaseViewCount(eventStatistic);
                            eventStatisticRepository.save(eventStatistic);
                        }
                );
    }

    private void increaseViewCount(final EventStatistic eventStatistic) {
        eventStatistic.increaseViewCount(LocalDate.now());
    }
}
