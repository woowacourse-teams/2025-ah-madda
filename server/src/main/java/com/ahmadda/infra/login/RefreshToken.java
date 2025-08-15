package com.ahmadda.infra.login;

import com.ahmadda.infra.login.exception.InvalidRefreshTokenRegistrationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id
    private String token;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private RefreshToken(final String token,
                         final Long memberId,
                         final String deviceId,
                         final LocalDateTime expiresAt) {
        validateRefreshToken(token);
        validateMemberId(memberId);
        validateDeviceId(deviceId);
        validateExpiresAt(expiresAt);

        this.token = token;
        this.memberId = memberId;
        this.deviceId = deviceId;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(final String token,
                                      final Long memberId,
                                      final String deviceId,
                                      final LocalDateTime expiresAt) {
        return new RefreshToken(token, memberId, deviceId, expiresAt);
    }

    private void validateRefreshToken(final String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenRegistrationException("refreshToken은 공백일 수 없습니다.");
        }
    }

    private void validateMemberId(final Long memberId) {
        if (memberId == null) {
            throw new InvalidRefreshTokenRegistrationException("memberId는 null일 수 없습니다.");
        }
    }

    private void validateDeviceId(final String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new InvalidRefreshTokenRegistrationException("deviceId는 null일 수 없습니다.");
        }
    }

    private void validateExpiresAt(final LocalDateTime expiresAt) {
        if (expiresAt == null) {
            throw new InvalidRefreshTokenRegistrationException("expiresAt은 null일 수 없습니다.");
        }

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenRegistrationException("expiresAt은 미래여야 합니다.");
        }
    }
}
