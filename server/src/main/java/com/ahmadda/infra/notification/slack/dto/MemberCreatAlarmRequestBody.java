package com.ahmadda.infra.notification.slack.dto;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MemberCreatAlarmRequestBody(
        List<Block> blocks,
        String channel
) {

    private record Block(
            String type,
            @JsonProperty("text")
            TextObject textObject
    ) {

    }

    private record TextObject(
            String type,
            String text
    ) {

    }

    public static MemberCreatAlarmRequestBody create(final MemberCreateAlarmPayload member, final String channelId) {
        String messageText = String.format(
                """
                        *🎉 새로운 회원 가입 알림*
                        - *이름*: %s
                        - *이메일*: %s
                        """,
                member.name(),
                member.email()
        );

        TextObject textObject = new TextObject("mrkdwn", messageText);
        Block mainBlock = new Block("section", textObject);

        return new MemberCreatAlarmRequestBody(List.of(mainBlock), channelId);
    }
}
