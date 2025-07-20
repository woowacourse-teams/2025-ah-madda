package com.ahmadda.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import java.time.LocalDateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PeriodTest {

    @ParameterizedTest
    @CsvSource({"2025-07-18T04:21, 2025-07-18T04:21", "2025-07-18T04:21, 2025-07-18T04:20"})
    void 종료_시간이_시작_시간보다_미래가_아니라면_예외가_발생한다(LocalDateTime start, LocalDateTime end) {
        assertThatThrownBy(() -> new Period(start, end))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("종료 시간은 시작 시간보다 미래여야 합니다.");
    }

    @ParameterizedTest
    @CsvSource({
            "2025-07-18T04:21, 2025-07-19T04:23, 2025-07-18T04:21, 2025-07-19T04:23, true",
            "2025-07-17T00:00, 2025-07-19T00:00, 2025-07-18T00:00, 2025-07-19T00:00, true",
            "2025-07-17T00:00, 2025-07-19T00:00, 2025-07-19T00:00, 2025-07-20T00:00, true",
            "2025-07-17T00:00, 2025-07-20T00:00, 2025-07-18T00:00, 2025-07-19T00:00, true",
            "2025-07-10T00:00, 2025-07-11T00:00, 2025-07-12T00:00, 2025-07-13T00:00, false",
            "2025-07-10T00:00, 2025-07-11T00:00, 2025-07-11T00:01, 2025-07-13T00:00, false",
    })
    void 두_기간이_겹치는지_확인할_수_있다(
            LocalDateTime start1,
            LocalDateTime end1,
            LocalDateTime start2,
            LocalDateTime end2,
            boolean expected
    ) {
        //given
        Period period1 = new Period(start1, end1);
        Period period2 = new Period(start2, end2);

        //when
        boolean result = period1.overlaps(period2);

        //then
        assertThat(result).isEqualTo(expected);
    }
}
