package com.ahmadda.infra.login;

import com.ahmadda.infra.login.exception.InvalidRefreshTokenException;
import com.ahmadda.infra.login.util.HashUtils;
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
                                      final String userAgent,
                                      final LocalDateTime expiresAt) {
        String encodedToken = HashUtils.sha256(token);
        String deviceId = HashUtils.sha256(userAgent);

        return new RefreshToken(encodedToken, memberId, deviceId, expiresAt);
    }

    private void validateRefreshToken(final String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("토큰은 공백일 수 없습니다.");
        }
    }

    private void validateMemberId(final Long memberId) {
        if (memberId == null) {
            throw new InvalidRefreshTokenException("멤버 식별자는 null일 수 없습니다.");
        }
    }

    private void validateDeviceId(final String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new InvalidRefreshTokenException("기기 식별자는 공백일 수 없습니다.");
        }
    }

    private void validateExpiresAt(final LocalDateTime expiresAt) {
        if (expiresAt == null) {
            throw new InvalidRefreshTokenException("만료 시간은 null일 수 없습니다.");
        }
    }
}
