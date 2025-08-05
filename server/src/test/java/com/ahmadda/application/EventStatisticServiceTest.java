package com.ahmadda.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.exception.UnauthorizedOperationException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
class EventStatisticServiceTest {

    @Test
    void 존재하지_않는_조직원일시_예외가_발생한다() {
        //given


        //when
        var sut = EventStatistic.create(event);

        //then
        assertThatThrownBy(() -> sut.findEventViewMetrics(notOrganizerOrganizationMember, LocalDate.now()))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("이벤트의 조회수는 이벤트의 주최자만 참조할 수 있습니다.");
    }

    @Test
    void 존재하지_않는_이벤트일시_예외가_발생한다(){
        //given

        //when
        var sut = EventStatistic.create(event);

        //then

    }

    @Test
    void 존재하지_않는_이벤트_조회수_정보일시_예외가_발생한다() {
        //given


        //when

        //then
    }
}
