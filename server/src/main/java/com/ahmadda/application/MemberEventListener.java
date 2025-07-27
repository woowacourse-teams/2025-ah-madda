package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.infra.slack.SlackReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class MemberEventListener {

    private final SlackReminder slackReminder;

    @EventListener
    public void handleMemberCreationEvent(final Member member) {
        slackReminder.alarmMemberCreation(member);
    }
}
