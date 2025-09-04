package com.ahmadda.infra.notification.push;

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
}
