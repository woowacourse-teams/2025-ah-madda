package com.ahmadda.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EventOperationPeriodTest {

    @Test
    void 이벤트_시작_시간이_이벤트_생성_요청_시점보다_과거라면_예외가_발생한다() {
        //given
        Period eventRegistrationPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 8, 0),
                LocalDateTime.of(2025, 7, 16, 8, 30)
        );
        Period eventPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 9, 0),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 10, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(eventRegistrationPeriod, eventPeriod, currentTime))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트 신청 시작 시간은 현재 시점보다 미래여야 합니다.");
    }

    @Test
    void 이벤트_신청_시작_시간이_이벤트_생성_요청_시점보다_과거인_경우_예외가_발생한다() {
        //given
        Period eventRegistrationPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 7, 59),
                LocalDateTime.of(2025, 7, 16, 8, 30)
        );
        Period eventPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 8, 30),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(eventRegistrationPeriod, eventPeriod, currentTime))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트 신청 시작 시간은 현재 시점보다 미래여야 합니다.");
    }

    @ParameterizedTest
    @CsvSource({"2025-07-16T08:30", "2025-07-16T08:31"})
    void 이벤트_신청_시작_시간이_이벤트_시작_시간보다_과거가_아닌_경우_예외가_발생한다(LocalDateTime registrationStart) {
        //given
        Period eventRegistrationPeriod = new Period(
                registrationStart,
                LocalDateTime.of(2025, 7, 16, 8, 50)
        );
        Period eventPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 8, 30),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(eventRegistrationPeriod, eventPeriod, currentTime))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("등록 기간과 이벤트 기간이 겹칠 수 없습니다.");
    }

    @ParameterizedTest
    @CsvSource({"2025-07-16T08:30", "2025-07-16T08:31"})
    void 이벤트_신청_마감_시간이_이벤트_시작_시간보다_미래라면_예외가_발생한다(LocalDateTime registrationEnd) {
        //given
        Period eventRegistrationPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 8, 0),
                registrationEnd
        );
        Period eventPeriod = new Period(
                LocalDateTime.of(2025, 7, 16, 8, 30),
                LocalDateTime.of(2025, 7, 16, 14, 0)
        );
        var currentTime = LocalDateTime.of(2025, 7, 16, 8, 0);

        //when //then
        assertThatThrownBy(() -> EventOperationPeriod.create(eventRegistrationPeriod, eventPeriod, currentTime))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("등록 기간과 이벤트 기간이 겹칠 수 없습니다.");
    }
}
