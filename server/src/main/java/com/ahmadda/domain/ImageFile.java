package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Getter
public class ImageFile {

    private static final List<String> ALLOW_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    private final String fileName;
    private final String contentType;
    private final long size;
    private final InputStream inputStream;

    private ImageFile(final String fileName, final String contentType, long size, final InputStream inputStream) {
        validateFileName(fileName);
        validateContentType(contentType);
        validateInputStream(inputStream);

        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.inputStream = inputStream;
    }

    public static ImageFile create(
            final String fileName,
            final String contentType,
            long size,
            final InputStream inputStream
    ) {
        return new ImageFile(fileName, contentType, size, inputStream);
    }

    private void validateFileName(final String fileName) {
        Assert.notBlank(fileName, "이미지 파일의 이름은 공백일 수 없습니다.");

        String extension = StringUtils.getFilenameExtension(fileName);
        if (!ALLOW_EXTENSIONS.contains(extension)) {
            throw new BusinessRuleViolatedException("이미지 파일의 확장자는 jpg, jpeg, png중 하나여야 합니다.");
        }
    }

    private void validateContentType(final String contentType) {
        Assert.notBlank(contentType, "이미지 컨텐츠 유형은 공백일 수 없습니다.");

        if (!contentType.startsWith("image")) {
            throw new BusinessRuleViolatedException("이미지 켄텐츠 유형이 아닙니다.");
        }
    }

    private void validateInputStream(final InputStream inputStream) {
        Assert.notNull(inputStream, "이미지 데이터가 비어 있을 수 없습니다.");
    }
}
