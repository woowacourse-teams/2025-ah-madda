package com.ahmadda.application;

import com.ahmadda.application.dto.EventRead;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.EventStatistic;
import com.ahmadda.domain.event.EventStatisticRepository;
import com.ahmadda.domain.member.Member;
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

    private void increaseViewCount(final EventStatistic eventStatistic, final Member member) {
        eventStatistic.increaseViewCount(
                LocalDate.now(),
                member
        );
    }
}
