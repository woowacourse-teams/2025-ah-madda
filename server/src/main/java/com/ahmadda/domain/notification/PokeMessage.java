package com.ahmadda.domain.notification;

public enum PokeMessage {
    RECOMMEND("%s님이 이벤트 참여를 추천 중이에요 🌈"),
    WAITING("%s님이 참여를 기다리고 있어요 ⏰"),
    ARRIVED("%s님에게 포키가 도착했어요! ✨"),
    HEART("%s님이 당신을 포키했어요! ❤️");

    private final String messageFormat;

    PokeMessage(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getMessage(final String from) {
        return String.format(messageFormat, from);
    }
}
