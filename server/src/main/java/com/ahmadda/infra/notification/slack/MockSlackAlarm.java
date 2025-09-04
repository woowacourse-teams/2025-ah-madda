package com.ahmadda.infra.notification.slack;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockSlackAlarm implements SlackAlarm {

    @Override
    public void alarmMemberCreation(final MemberCreateAlarmPayload memberCreateAlarmPayload) {
        log.info("회원가입 유저 정보 : {} 프로덕션이 아니어서 슬랙으로 알람 보내지 않음", memberCreateAlarmPayload.toString());
    }
}
