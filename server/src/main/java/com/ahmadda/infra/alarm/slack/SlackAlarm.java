package com.ahmadda.infra.alarm.slack;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;

public interface SlackAlarm {

    void alarmMemberCreation(final MemberCreateAlarmPayload memberCreateAlarmPayload);
}
