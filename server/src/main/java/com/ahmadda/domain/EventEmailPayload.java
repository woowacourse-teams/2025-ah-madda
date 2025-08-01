package com.ahmadda.domain;

import com.ahmadda.domain.util.Assert;

public record EventEmailPayload(
        Subject subject,
        Body body
) {

    public static EventEmailPayload of(final Event event, final String content) {
        Subject subject = new Subject(
                event.getOrganization()
                        .getName(),
                event.getOrganizer()
                        .getNickname(),
                event.getTitle()
        );

        Body body = new Body(
                content,
                event.getOrganization()
                        .getName(),
                event.getTitle(),
                event.getOrganizerNickname(),
                event.getPlace(),
                event.getRegistrationStart(),
                event.getRegistrationEnd(),
                event.getEventStart(),
                event.getEventEnd(),
                event.getId()
        );

        return new EventEmailPayload(subject, body);
    }

    public record Subject(
            String organizationName,
            String organizerNickname,
            String eventTitle
    ) {

        public Subject {
            Assert.notBlank(organizationName, "조직 이름은 공백일 수 없습니다.");
            Assert.notBlank(organizerNickname, "주최자 닉네임은 공백일 수 없습니다.");
            Assert.notBlank(eventTitle, "이벤트 제목은 공백일 수 없습니다.");
        }
    }

    public record Body(
            String content,
            String organizationName,
            String title,
            String organizerNickname,
            String place,
            Object registrationStart,
            Object registrationEnd,
            Object eventStart,
            Object eventEnd,
            Long eventId
    ) {

        public Body {
            Assert.notBlank(content, "이메일 본문은 공백일 수 없습니다.");
            Assert.notBlank(organizationName, "조직 이름은 공백일 수 없습니다.");
            Assert.notBlank(title, "이벤트 제목은 공백일 수 없습니다.");
            Assert.notBlank(organizerNickname, "주최자 닉네임은 공백일 수 없습니다.");
            Assert.notNull(place, "장소는 null일 수 없습니다.");
            Assert.notNull(registrationStart, "신청 시작 시간은 null일 수 없습니다.");
            Assert.notNull(registrationEnd, "신청 종료 시간은 null일 수 없습니다.");
            Assert.notNull(eventStart, "이벤트 시작 시간은 null일 수 없습니다.");
            Assert.notNull(eventEnd, "이벤트 종료 시간은 null일 수 없습니다.");
            Assert.notNull(eventId, "이벤트 ID는 null일 수 없습니다.");
        }
    }
}
