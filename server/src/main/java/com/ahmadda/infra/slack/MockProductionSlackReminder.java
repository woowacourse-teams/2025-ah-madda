package com.ahmadda.infra.slack;

import com.ahmadda.application.dto.MemberCreateAlarmDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockProductionSlackReminder implements SlackReminder {

    @Override
    public void alarmMemberCreation(final MemberCreateAlarmDto memberCreateAlarmDto) {
        log.info("Member created but Slack alarm not sent (non-prod environment): {}", memberCreateAlarmDto);
    }
}
