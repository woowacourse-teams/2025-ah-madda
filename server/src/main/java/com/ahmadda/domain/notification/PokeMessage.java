package com.ahmadda.domain.notification;

public enum PokeMessage {
    RECOMMEND("%s님이 이벤트 참여를 추천했어요! 🌈"),
    WAITING("%s님이 참여를 기다려요 ⏰"),
    ARRIVED("%s님의 포키가 도착했어요! ✨"),
    HEART("%s님이 당신을 포키했어요! ❤️");

    private final String messageFormat;

    PokeMessage(final String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getMessage(final String from) {
        return String.format(messageFormat, from);
    }
}
