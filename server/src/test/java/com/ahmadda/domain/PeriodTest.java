package com.ahmadda.domain;

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
}
