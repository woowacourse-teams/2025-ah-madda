package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeriodTest {

    @ParameterizedTest
    @CsvSource({"2025-07-18T04:21, 2025-07-18T04:21", "2025-07-18T04:21, 2025-07-18T04:20"})
    void 종료_시간이_시작_시간보다_미래가_아니라면_예외가_발생한다(LocalDateTime start, LocalDateTime end) {
        assertThatThrownBy(() -> Period.create(start, end))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("종료 시간은 시작 시간보다 미래여야 합니다.");
    }

    @ParameterizedTest
    @MethodSource("periods")
    void 두_기간이_겹치는지_확인할_수_있다(Period period1, Period period2, boolean expected) {
        // when
        var result = period1.isOverlappedWith(period2);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("provideDateTimesForIncludesTest")
    void 특정_날짜가_기간에_포함되는지_알_수_있다(LocalDateTime testDate, boolean expected) {
        var start = LocalDateTime.of(2025, 7, 1, 12, 0);
        var end = LocalDateTime.of(2025, 7, 1, 14, 0);
        var sut = Period.create(start, end);

        // when
        var result = sut.includes(testDate);

        // then
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> periods() {
        return Stream.of(
                Arguments.of(
                        Period.create(
                                LocalDateTime.of(2025, 7, 18, 4, 21),
                                LocalDateTime.of(2025, 7, 19, 4, 23)
                        ),
                        Period.create(
                                LocalDateTime.of(2025, 7, 18, 4, 21),
                                LocalDateTime.of(2025, 7, 19, 4, 23)
                        ),
                        true
                ),
                Arguments.of(
                        Period.create(
                                LocalDateTime.of(2025, 7, 17, 0, 0),
                                LocalDateTime.of(2025, 7, 19, 0, 0)
                        ),
                        Period.create(
                                LocalDateTime.of(2025, 7, 18, 0, 0),
                                LocalDateTime.of(2025, 7, 19, 0, 0)
                        ),
                        true
                ),
                Arguments.of(
                        Period.create(
                                LocalDateTime.of(2025, 7, 17, 0, 0),
                                LocalDateTime.of(2025, 7, 19, 0, 0)
                        ),
                        Period.create(
                                LocalDateTime.of(2025, 7, 19, 0, 0),
                                LocalDateTime.of(2025, 7, 20, 0, 0)
                        ),
                        false
                ),
                Arguments.of(
                        Period.create(
                                LocalDateTime.of(2025, 7, 17, 0, 0),
                                LocalDateTime.of(2025, 7, 20, 0, 0)
                        ),
                        Period.create(
                                LocalDateTime.of(2025, 7, 18, 0, 0),
                                LocalDateTime.of(2025, 7, 19, 0, 0)
                        ),
                        true
                ),
                Arguments.of(
                        Period.create(
                                LocalDateTime.of(2025, 7, 10, 0, 0),
                                LocalDateTime.of(2025, 7, 11, 0, 0)
                        ),
                        Period.create(
                                LocalDateTime.of(2025, 7, 12, 0, 0),
                                LocalDateTime.of(2025, 7, 13, 0, 0)
                        ),
                        false
                ),
                Arguments.of(
                        Period.create(
                                LocalDateTime.of(2025, 7, 10, 0, 0),
                                LocalDateTime.of(2025, 7, 11, 0, 0)
                        ),
                        Period.create(
                                LocalDateTime.of(2025, 7, 11, 0, 1),
                                LocalDateTime.of(2025, 7, 13, 0, 0)
                        ),
                        false
                )
        );
    }

    private static Stream<Arguments> provideDateTimesForIncludesTest() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2025, 7, 1, 11, 59), false),
                Arguments.of(LocalDateTime.of(2025, 7, 1, 12, 0), true),
                Arguments.of(LocalDateTime.of(2025, 7, 1, 13, 0), true),
                Arguments.of(LocalDateTime.of(2025, 7, 1, 14, 0), true),
                Arguments.of(LocalDateTime.of(2025, 7, 1, 14, 1), false)
        );
    }
}
