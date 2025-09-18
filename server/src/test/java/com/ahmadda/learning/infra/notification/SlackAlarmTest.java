package com.ahmadda.learning.infra.notification;


import com.ahmadda.annotation.LearningTest;
import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.domain.member.Member;
import com.ahmadda.infra.notification.slack.SlackAlarm;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Disabled
@LearningTest
@TestPropertySource(properties = "slack.mock=false")
class SlackAlarmTest {

    @Autowired
    SlackAlarm slackAlarm;

    @Test
    void 실제_슬랙으로_알람을_전송한다() {
        slackAlarm.alarmMemberCreation(
                MemberCreateAlarmPayload.from(Member.create(
                        "asdf",
                        "asdf@naver.com",
                        "testPicture"
                )));
    }
}
