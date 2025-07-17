package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrganizationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void 조직_생성시_설명이_비어있으면_예외가_발생한다(String blankDescription) {
        // when // then
        assertThatThrownBy(() -> Organization.create("정상 이름", blankDescription, "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 조직_생성시_이름이_규칙보다_길면_예외가_발생한다() {
        // given
        var longName = "스무글자를넘어가는엄청나게긴조직이름입니다";

        // when // then
        assertThatThrownBy(() -> Organization.create(longName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 조직_생성시_이름이_규칙보다_짧으면_예외가_발생한다() {
        // given
        var shortName = "한";

        // when // then
        assertThatThrownBy(() -> Organization.create(shortName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void 조직_생성시_이름이_비어있으면_예외가_발생한다(String blankName) {
        // when // then
        assertThatThrownBy(() -> Organization.create(blankName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }
}
