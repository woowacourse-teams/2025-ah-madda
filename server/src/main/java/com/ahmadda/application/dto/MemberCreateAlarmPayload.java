package com.ahmadda.application.dto;

import com.ahmadda.domain.member.Member;

public record MemberCreateAlarmPayload(
        String name,
        String email
) {

    public static MemberCreateAlarmPayload from(final Member alarmMember) {
        return new MemberCreateAlarmPayload(alarmMember.getName(), alarmMember.getEmail());
    }
}
