package com.ahmadda.infra.slack.dto;

import com.ahmadda.application.dto.MemberCreateAlarmDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MemberCreationAlarmPayload(
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

    public static MemberCreationAlarmPayload create(final MemberCreateAlarmDto member, final String channelId) {
        var messageText = String.format(
                """
                        *🎉 새로운 회원 가입 알림*
                        - *이름*: %s
                        - *이메일*: %s
                        """,
                member.name(),
                member.email()
        );

        var textObject = new TextObject("mrkdwn", messageText);
        var mainBlock = new Block("section", textObject);

        return new MemberCreationAlarmPayload(List.of(mainBlock), channelId);
    }
}
