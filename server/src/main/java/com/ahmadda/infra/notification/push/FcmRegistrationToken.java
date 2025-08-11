package com.ahmadda.infra.notification.push;

import com.ahmadda.infra.notification.push.exception.InvalidFcmRegistrationTokenException;
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
public class FcmRegistrationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_registration_token_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // TODO. 추후 기기당 토큰의 중복을 허용할지 여부를 결정해야 함
    @Column(nullable = false)
    private String registrationToken;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    private FcmRegistrationToken(
            final Long memberId,
            final String registrationToken,
            final LocalDateTime timeStamp
    ) {
        validateMemberId(memberId);
        validatePushToken(registrationToken);
        validateTimeStamp(timeStamp);

        this.memberId = memberId;
        this.registrationToken = registrationToken;
        this.timeStamp = timeStamp;
    }

    public static FcmRegistrationToken create(
            final Long memberId,
            final String registrationToken,
            final LocalDateTime timeStamp
    ) {
        return new FcmRegistrationToken(memberId, registrationToken, timeStamp);
    }

    public static FcmRegistrationToken createNow(
            final Long memberId,
            final String registrationToken
    ) {
        return new FcmRegistrationToken(memberId, registrationToken, LocalDateTime.now());
    }

    private void validateMemberId(final Long memberId) {
        if (memberId == null) {
            throw new InvalidFcmRegistrationTokenException("memberId는 null일 수 없습니다.");
        }
    }

    private void validatePushToken(final String registrationToken) {
        if (registrationToken == null || registrationToken.isBlank()) {
            throw new InvalidFcmRegistrationTokenException("등록 토큰은 공백일 수 없습니다.");
        }
    }

    private void validateTimeStamp(final LocalDateTime timeStamp) {
        if (timeStamp == null) {
            throw new InvalidFcmRegistrationTokenException("타임스탬프는 null일 수 없습니다.");
        }
    }
}
