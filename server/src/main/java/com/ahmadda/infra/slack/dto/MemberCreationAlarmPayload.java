package com.ahmadda.infra.slack.dto;

import com.ahmadda.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MemberCreationAlarmPayload(
        List<Block> blocks,
        String channel
) {

    private record Block(
            String type,
            @JsonProperty("text") // JSON 필드명과 일치시키기 위한 어노테이션
            TextObject textObject
    ) {

    }

    private record TextObject(
            String type,
            String text
    ) {

    }

    public static MemberCreationAlarmPayload create(final Member member, final String channelId) {
        var messageText = String.format(
                """
                        *🎉 새로운 회원 가입 알림*
                        - *이름*: %s
                        - *이메일*: %s
                        """,
                member.getName(),
                member.getEmail()
        );

        var textObject = new TextObject("mrkdwn", messageText);
        var mainBlock = new Block("section", textObject);

        return new MemberCreationAlarmPayload(List.of(mainBlock), channelId);
    }
}
