package com.ahmadda.infra.auth;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private RefreshToken(
            final String token,
            final Long memberId,
            final String deviceId,
            final LocalDateTime expiresAt
    ) {
        this.token = token;
        this.memberId = memberId;
        this.deviceId = deviceId;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(
            final String encodedToken,
            final Long memberId,
            final String deviceId,
            final LocalDateTime expiresAt
    ) {
        return new RefreshToken(encodedToken, memberId, deviceId, expiresAt);
    }
}
