package com.ahmadda.learning;


import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.domain.Member;
import com.ahmadda.infra.alarm.slack.SlackAlarm;
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
class SlackAlarmTest {

    @Autowired
    SlackAlarm slackAlarm;

    @Test
    void 실제_슬랙_리마인드_테스트() {
        slackAlarm.alarmMemberCreation(MemberCreateAlarmPayload.from(Member.create("asdf", "asdf@naver.com")));
    }
}
