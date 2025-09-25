package com.ahmadda.infra.notification.slack;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoopSlackAlarm implements SlackAlarm {

    @Override
    public void alarmMemberCreation(final MemberCreateAlarmPayload memberCreateAlarmPayload) {
        log.info("[Noop Slack] Member: {}", memberCreateAlarmPayload);
    }
}
