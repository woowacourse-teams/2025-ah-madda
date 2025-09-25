package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.UnprocessableEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class OrganizationImageFileTest {

    private InputStream validInputStream;

    @BeforeEach
    void setUp() {
        validInputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpg", "jpeg", "png"})
    void 허용_확장자로_이미지_파일을_생성할_수_있다(String extension) {
        // given
        var fileName = "test." + extension;
        var contentType = "image/" + extension;
        var size = 1024;

        // when
        var imageFile = OrganizationImageFile.create(fileName, contentType, size, validInputStream);

        // then
        assertSoftly(softly -> {
            softly.assertThat(imageFile.getFileName())
                    .isEqualTo(fileName);
            softly.assertThat(imageFile.getContentType())
                    .isEqualTo(contentType);
            softly.assertThat(imageFile.getSize())
                    .isEqualTo(size);
            softly.assertThat(imageFile.getInputStream())
                    .isEqualTo(validInputStream);
        });
    }

    @Test
    void 허용되지_않는_확장자를_사용하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() ->
                OrganizationImageFile.create("test.gif", "image/gif", 1024, validInputStream)
        )
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미지 파일의 확장자는 jpg, jpeg, png중 하나여야 합니다.");
    }

    @Test
    void 컨텐츠_타입이_이미지가_아니면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() ->
                OrganizationImageFile.create("test.jpg", "text/plain", 1024, validInputStream)
        )
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미지 켄텐츠 유형이 아닙니다.");
    }
}
