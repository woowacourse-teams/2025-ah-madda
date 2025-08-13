package com.ahmadda.infra.login;

import com.ahmadda.infra.login.jwt.JwtProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id
    private String token;

    @Column(nullable = false)
    private Long memberId;

    private RefreshToken(final String token, final Long memberId) {
        this.token = token;
        this.memberId = memberId;
    }

    public static RefreshToken create(final Long memberId, final JwtProvider jwtProvider) {
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        return new RefreshToken(refreshToken, memberId);
    }
}
