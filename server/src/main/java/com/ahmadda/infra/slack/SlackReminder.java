package com.ahmadda.infra.slack;

import com.ahmadda.application.dto.MemberCreateAlarmDto;

public interface SlackReminder {

    void alarmMemberCreation(final MemberCreateAlarmDto memberCreateAlarmDto);
}
