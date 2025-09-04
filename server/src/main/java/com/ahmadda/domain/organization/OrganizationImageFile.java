package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.UnprocessableEntityException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Getter
public class OrganizationImageFile {

    private static final List<String> ALLOW_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final String fileName;
    private final String contentType;
    private final long size;
    private final InputStream inputStream;

    private OrganizationImageFile(
            final String fileName,
            final String contentType,
            final long size,
            final InputStream inputStream
    ) {
        validateFileName(fileName);
        validateContentType(contentType);
        validateSize(size);

        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.inputStream = inputStream;
    }

    public static OrganizationImageFile create(
            final String fileName,
            final String contentType,
            final long size,
            final InputStream inputStream
    ) {
        return new OrganizationImageFile(fileName, contentType, size, inputStream);
    }

    private void validateFileName(final String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension == null || !ALLOW_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new UnprocessableEntityException("이미지 파일의 확장자는 jpg, jpeg, png중 하나여야 합니다.");
        }
    }

    private void validateContentType(final String contentType) {
        if (!contentType.startsWith("image")) {
            throw new UnprocessableEntityException("이미지 켄텐츠 유형이 아닙니다.");
        }
    }

    private void validateSize(final long size) {
        if (size > MAX_FILE_SIZE) {
            throw new UnprocessableEntityException(
                    String.format("이미지 파일 크기는 %dMB를 초과할 수 없습니다.", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
    }
}
