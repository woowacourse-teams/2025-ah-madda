package com.ahmadda.application;

import com.ahmadda.application.dto.EventRead;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
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
    public void onEventReaded(final EventRead eventRead) {
        Member member = memberRepository.findById(eventRead.loginMember()
                        .memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        eventStatisticRepository.findByEventId(eventRead.eventId())
                .ifPresentOrElse(
                        eventStatistic -> {
                            increaseViewCount(eventStatistic, member);
                        },
                        () -> {
                            Event event = eventRepository.findById(eventRead.eventId())
                                    .orElseThrow((() -> new NotFoundException("존재하지 않는 이벤트입니다")));

                            EventStatistic eventStatistic = EventStatistic.create(event);

                            increaseViewCount(eventStatistic, member);
                            eventStatisticRepository.save(eventStatistic);
                        }
                );
    }

    private static void increaseViewCount(EventStatistic eventStatistic, Member member) {
        eventStatistic.increaseViewCount(
                LocalDate.now(),
                member
        );
    }
}
