package com.ahmadda.infra.slack;

import com.ahmadda.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Profile("prod")
class SlackReminderTest {

    @Autowired
    private SlackReminder slackReminder;

    @Test
    void 로그인_생성_이벤트_발생시_슬랙에_알람을_보낸다() {

        // given
        var member = Member.create("test-user", "test-user@naver.com");

        // when //then
        assertDoesNotThrow(() -> slackReminder.alarmMemberCreation(member));
    }
}
