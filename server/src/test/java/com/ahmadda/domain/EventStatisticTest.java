package com.ahmadda.domain;

import org.junit.jupiter.api.Test;

class EventStatisticTest {

    @Test
    void 이벤트_주최자가_아니라면_조회수_요청시_예외를_반환한다() {

    }

    @Test
    void 조회수가_없는_날짜에는_조회수가_0으로_출력된다() {

    }

    @Test
    void 조회수는_오늘이_이벤트_종료시간_이전이면_이벤트_생성시점부터_오늘까지_조회수를_반환한다() {

    }

    @Test
    void 조회수는_오늘이_이벤트_종료시간_이후면_이벤트_생성시점부터_이벤트_종료시간까지_반환한다() {

    }

    @Test
    void 조회수는_이벤트_상세정보를_볼때_늘어난다() {

    }
}
