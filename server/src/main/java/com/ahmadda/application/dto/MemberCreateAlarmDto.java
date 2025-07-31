package com.ahmadda.application.dto;

import com.ahmadda.domain.Member;

public record MemberCreateAlarmDto(
        String name,
        String email
) {

    public static MemberCreateAlarmDto from(final Member alarmMember) {
        return new MemberCreateAlarmDto(alarmMember.getName(), alarmMember.getEmail());
    }
}
