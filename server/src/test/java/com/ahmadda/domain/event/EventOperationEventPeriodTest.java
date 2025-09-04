package com.ahmadda.domain.event;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventOperationEventPeriodTest {

    @Test
    void 정상적인_이벤트_운영_기간을_생성한다() {
        // given
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);
        var registrationPeriod = EventPeriod.create(
                currentTime.plusDays(1),
                currentTime.plusDays(5)
        );
        var eventPeriod = EventPeriod.create(
                currentTime.plusDays(6),
                currentTime.plusDays(7)
        );

        // when // then
        assertThatCode(() -> EventOperationPeriod.create(
                registrationPeriod.start(),
                registrationPeriod.end(),
                eventPeriod.start(),
                eventPeriod.end(),
                currentTime
        )).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"2025-07-16T08:30", "2025-07-16T08:31"})
    void 이벤트_신청_시작_시간이_이벤트_시작_시간보다_과거가_아닌_경우_예외가_발생한다(LocalDateTime registrationStart) {
        //given
        var registrationPeriod = EventPeriod.create(
                registrationStart,
                LocalDateTime.of(2025, 7, 16, 8, 50)
        );
        var eventPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 8, 30),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(
                registrationPeriod.start(),
                registrationPeriod.end(),
                eventPeriod.start(),
                eventPeriod.end(),
                currentTime
        ))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("신청 기간과 이벤트 기간이 겹칠 수 없습니다.");
    }

    @ParameterizedTest
    @CsvSource({"2025-07-16T08:31", "2025-07-16T08:32"})
    void 이벤트_신청_마감_시간이_이벤트_시작_시간보다_미래라면_예외가_발생한다(LocalDateTime registrationEnd) {
        //given
        var registrationPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 8, 0),
                registrationEnd
        );
        var eventPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 8, 30),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(
                registrationPeriod.start(),
                registrationPeriod.end(),
                eventPeriod.start(),
                eventPeriod.end(),
                currentTime
        ))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("신청 기간과 이벤트 기간이 겹칠 수 없습니다.");
    }

    @Test
    void 이벤트_시작_기간이_현재_시점보다_미래가_아니라면_예외가_발생한다() {
        //given
        var registrationPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 8, 0),
                LocalDateTime.of(2025, 7, 16, 8, 30)
        );
        var eventPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 7, 59),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);

        //when  //then
        assertThatThrownBy(() -> EventOperationPeriod.create(
                registrationPeriod.start(),
                registrationPeriod.end(),
                eventPeriod.start(),
                eventPeriod.end(),
                currentTime
        ))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트 시작 시간은 현재 시점보다 미래여야 합니다.");
    }

    @Test
    void 이벤트_신청_기간이_이벤트_기간보다_앞선다면_예외가_발생한다() {
        //given
        var registrationPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 8, 0),
                LocalDateTime.of(2025, 7, 16, 8, 30)
        );
        var eventPeriod = EventPeriod.create(
                LocalDateTime.of(2025, 7, 16, 2, 31),
                LocalDateTime.of(2025, 7, 16, 4, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 1, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(
                registrationPeriod.start(),
                registrationPeriod.end(),
                eventPeriod.start(),
                eventPeriod.end(),
                currentTime
        ))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("신청 기간은 이벤트 기간보다 앞서야 합니다.");

    }
}
