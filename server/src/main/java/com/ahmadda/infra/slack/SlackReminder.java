package com.ahmadda.infra.slack;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;

public interface SlackReminder {

    void alarmMemberCreation(final MemberCreateAlarmPayload memberCreateAlarmPayload);
}
