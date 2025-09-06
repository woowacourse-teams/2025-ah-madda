package com.ahmadda.infra.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashEncoderTest {

    private final HashEncoder sut = new HashEncoder();

    @Test
    void 해시_결과는_64자리_이어야_한다() {
        // given
        var token = "token";

        // when
        var hash = sut.sha256(token);

        assertThat(hash).hasSize(64);
    }

    @Test
    void 해시_결과는_결정적_이어야한다() {
        // given
        var input = "repeat";

        // When
        var hash1 = sut.sha256(input);
        var hash2 = sut.sha256(input);

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }
}