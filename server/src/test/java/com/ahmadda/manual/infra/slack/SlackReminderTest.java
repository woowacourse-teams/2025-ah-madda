package com.ahmadda.manual.infra.slack;

import com.ahmadda.application.dto.MemberCreateAlarmDto;
import com.ahmadda.domain.Member;
import com.ahmadda.infra.slack.AsyncSlackReminder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//TODO Disable
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SlackReminderTest {

    @Autowired
    private AsyncSlackReminder asyncSlackReminder;

    @Test
    void 로그인_생성_이벤트_발생시_슬랙에_알람을_보낸다() {

        // given
        var member = MemberCreateAlarmDto.from(Member.create("test-user", "test-user@naver.com"));

        // when //then
        assertDoesNotThrow(() -> asyncSlackReminder.alarmMemberCreation(member));
    }
}
