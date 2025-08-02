package com.ahmadda.learning;


import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.domain.Member;
import com.ahmadda.infra.slack.SlackReminder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "slack.mock=false")
@Transactional
@Disabled
class SlackReminderTest {

    @Autowired
    private SlackReminder slackReminder;

    @Test
    void 실제_슬랙_리마인드_테스트() {
        // when // then
        slackReminder.alarmMemberCreation(MemberCreateAlarmPayload.from(Member.create("asdf", "asdf@naver.com")));
    }
}
