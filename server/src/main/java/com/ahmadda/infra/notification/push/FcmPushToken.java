package com.ahmadda.infra.notification.push;

import com.ahmadda.infra.notification.push.exception.InvalidFcmPushTokenException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmPushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_push_token_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // TODO. 추후 기기당 토큰의 중복을 허용할지 여부를 결정해야 함
    @Column(nullable = false, unique = true)
    private String pushToken;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    private FcmPushToken(
            final Long memberId,
            final String pushToken,
            final LocalDateTime timeStamp
    ) {
        validateMemberId(memberId);
        validatePushToken(pushToken);
        validateTimeStamp(timeStamp);

        this.memberId = memberId;
        this.pushToken = pushToken;
        this.timeStamp = timeStamp;
    }

    public static FcmPushToken create(
            final Long memberId,
            final String pushToken,
            final LocalDateTime timeStamp
    ) {
        return new FcmPushToken(memberId, pushToken, timeStamp);
    }

    private void validateMemberId(final Long memberId) {
        if (memberId == null) {
            throw new InvalidFcmPushTokenException("memberId는 null일 수 없습니다.");
        }
    }

    private void validatePushToken(final String pushToken) {
        if (pushToken == null || pushToken.isBlank()) {
            throw new InvalidFcmPushTokenException("푸시 토큰은 공백일 수 없습니다.");
        }
    }

    private void validateTimeStamp(final LocalDateTime timeStamp) {
        if (timeStamp == null) {
            throw new InvalidFcmPushTokenException("timeStamp는 null일 수 없습니다.");
        }
    }
}
