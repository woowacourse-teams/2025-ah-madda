package com.ahmadda.infra.slack;

import com.ahmadda.application.dto.MemberCreateAlarmDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockSlackReminder implements SlackReminder {

    @Override
    public void alarmMemberCreation(final MemberCreateAlarmDto memberCreateAlarmDto) {
        log.info("회원가입 유저 정보 : {} 프로덕션이 아니어서 슬랙으로 알람 보내지 않음", memberCreateAlarmDto.toString());
    }
}
